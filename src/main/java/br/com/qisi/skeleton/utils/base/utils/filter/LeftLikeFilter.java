package br.com.qisi.skeleton.utils.base.utils.filter;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class LeftLikeFilter<T> implements BaseQueryFilter<T> {

  private static final String LEFT_LIKE_OPERATOR = "%<";
  private final Predicate predicate;
  private final CriteriaBuilder criteriaBuilder;
  private final Root<T> root;
  private final List<QueryParam> queryParams;
  private final BaseQueryFilter<T> nextFilter;

  public LeftLikeFilter(Predicate predicate, CriteriaBuilder criteriaBuilder, Root<T> root, List<QueryParam> queryParams, BaseQueryFilter<T> nextFilter) {
    this.predicate = predicate;
    this.criteriaBuilder = criteriaBuilder;
    this.root = root;
    this.queryParams = queryParams;
    this.nextFilter = nextFilter;
  }

  @Override
  public Predicate agregatePredicate() {

    for (QueryParam queryParam : queryParams) {

      if (LEFT_LIKE_OPERATOR.equalsIgnoreCase(queryParam.getOperation())) {

        String[] splitedFieldName = queryParam.getFieldName().split("[.]");

        if(this.needJoin(splitedFieldName)) {
          Join<T, ?> join = doJoin(splitedFieldName, root);
          queryParam.setFieldName(splitedFieldName[1]);
          add(predicate, criteriaBuilder.like(criteriaBuilder.lower(join.get(queryParam.getFieldName())), "%" + queryParam.getFieldValue().toLowerCase()));
        } else {
          add(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get(queryParam.getFieldName())), "%" + queryParam.getFieldValue().toLowerCase()));
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
