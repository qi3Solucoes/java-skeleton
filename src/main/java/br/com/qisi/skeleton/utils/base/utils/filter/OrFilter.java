package br.com.qisi.skeleton.utils.base.utils.filter;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class OrFilter<T> implements BaseQueryFilter<T> {
  private static final String OR_OPERATOR = "OR";
  private final Predicate predicate;
  private final CriteriaBuilder criteriaBuilder;
  private final Root<T> root;
  private final List<QueryParam> queryParams;
  private final BaseQueryFilter<T> nextFilter;

  public OrFilter(Predicate predicate, CriteriaBuilder criteriaBuilder, Root<T> root, List<QueryParam> queryParams, BaseQueryFilter<T> nextFilter) {
    this.predicate = predicate;
    this.criteriaBuilder = criteriaBuilder;
    this.root = root;
    this.queryParams = queryParams;
    this.nextFilter = nextFilter;
  }

  @Override
  public Predicate agregatePredicate() {

    for (QueryParam queryParam : queryParams) {

      if (OR_OPERATOR.equalsIgnoreCase(queryParam.getOperation())) {

        String[] splitedFieldName = queryParam.getFieldName().split("[.]");

        if (this.needJoin(splitedFieldName)) {
          Join<T, ?> join = doJoin(splitedFieldName, root);
          queryParam.setFieldName(splitedFieldName[1]);
          add(predicate, criteriaBuilder.or(
                  criteriaBuilder.equal(join.get(queryParam.getFieldName()), queryParam.getFieldValue()),
                  criteriaBuilder.equal(join.get(queryParam.getFieldName()), queryParam.getFieldValueSecondary())
            )
          );
        } else {
          add(predicate, criteriaBuilder.or(
                  criteriaBuilder.equal(root.get(queryParam.getFieldName()), queryParam.getFieldValue()),
                  criteriaBuilder.equal(root.get(queryParam.getFieldName()), queryParam.getFieldValueSecondary())
                  )
          );
        }

      }
    }

    if (!Objects.isNull(this.nextFilter)) {
      return this.nextFilter.agregatePredicate();
    } else {
      return predicate;
    }
  }
}
