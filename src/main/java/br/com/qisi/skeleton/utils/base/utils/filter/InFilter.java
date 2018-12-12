package br.com.qisi.skeleton.utils.base.utils.filter;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class InFilter<T> implements BaseQueryFilter<T> {

  private static final String IN_OPERAPOR = ":[";
  private final Predicate predicate;
  private final CriteriaBuilder criteriaBuilder;
  private final Root<T> root;
  private final List<QueryParam> queryParams;
  private final BaseQueryFilter nextFilter;

  public InFilter(Predicate predicate, CriteriaBuilder criteriaBuilder, Root<T> root, List<QueryParam> queryParams, BaseQueryFilter nextFilter) {
    this.predicate = predicate;
    this.criteriaBuilder = criteriaBuilder;
    this.root = root;
    this.queryParams = queryParams;
    this.nextFilter = nextFilter;
  }

  @Override
  public Predicate agregatePredicate() {
    for (QueryParam queryParam : queryParams) {

      if (IN_OPERAPOR.equalsIgnoreCase(queryParam.getOperation())) {

        String[] splitedFieldName = queryParam.getFieldName().split("[.]");

        if(this.needJoin(splitedFieldName)) {
          Join<T, ?> join = doJoin(splitedFieldName, root);
          queryParam.setFieldName(splitedFieldName[1]);
          add(predicate, join.get(queryParam.getFieldName()).in(queryParam.getInValues()));
        } else {
          add(predicate, root.get(queryParam.getFieldName()).in(queryParam.getInValues()));
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
