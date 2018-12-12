package br.com.qisi.skeleton.utils.base.utils.filter;


import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EqualsFilter<T> implements BaseQueryFilter<T> {

  private static final String EQUALS_OPERATOR = ":";
  private Predicate predicate;
  private final CriteriaBuilder criteriaBuilder;
  private final Root<T> root;
  private final List<QueryParam> queryParams;
  private final Class<T> clazz;
  private final BaseQueryFilter nextFilter;

  public EqualsFilter(Predicate predicate, CriteriaBuilder criteriaBuilder, Root<T> root, List<QueryParam> queryParams, Class<T> clazz, BaseQueryFilter nextFilter) {
    this.predicate = predicate;
    this.criteriaBuilder = criteriaBuilder;
    this.root = root;
    this.queryParams = queryParams;
    this.clazz = clazz;
    this.nextFilter = nextFilter;
  }

  @Override
  public Predicate agregatePredicate() {

    for (QueryParam queryParam : queryParams) {

      if (EQUALS_OPERATOR.equalsIgnoreCase(queryParam.getOperation())) {

        String[] splitedFieldName = queryParam.getFieldName().split("[.]");

        Object value = getValue(Arrays.asList(clazz.getDeclaredFields()), queryParam, splitedFieldName, clazz);

        if (this.needJoin(splitedFieldName)) {
          Join<T, ?> join = doJoin(splitedFieldName, root);
          queryParam.setFieldName(splitedFieldName[1]);
          add(predicate, criteriaBuilder.equal(join.get(queryParam.getFieldName()), value));
        } else {
          add(predicate, criteriaBuilder.equal(root.get(queryParam.getFieldName()), value));
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
