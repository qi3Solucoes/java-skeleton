package br.com.qisi.skeleton.utils.base.utils.filter;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class BetweenFilter<T> implements BaseQueryFilter<T> {
  private static final String BTW_OPERATION =  "BTW";
  private final Predicate predicate;
  private final CriteriaBuilder criteriaBuilder;
  private final Root<T> root;
  private final List<QueryParam> queryParams;
  private Class<T> clazz;
  private final BaseQueryFilter<T> nextFilter;

  public BetweenFilter(Predicate predicate, CriteriaBuilder criteriaBuilder, Root<T> root, List<QueryParam> queryParams, Class<T> clazz, BaseQueryFilter<T> nextFilter) {
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

      if (BTW_OPERATION.equalsIgnoreCase(queryParam.getOperation())) {

        String[] splitedFieldName = queryParam.getFieldName().split("[.]");

        if (this.needJoin(splitedFieldName)) {

          Join<T, ?> join = doJoin(splitedFieldName, root);
          queryParam.setFieldName(splitedFieldName[1]);


          Class fieldClass = this.getFieldClass(queryParam.getFieldName(), clazz);

          if(LocalDateTime.class == fieldClass) {
            add(predicate, criteriaBuilder.greaterThanOrEqualTo(join.get(queryParam.getFieldName()), LocalDateTime.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE_TIME)));
            add(predicate, criteriaBuilder.lessThanOrEqualTo(join.get(queryParam.getFieldName()), LocalDateTime.parse(queryParam.getFieldValueSecondary(), DateTimeFormatter.ISO_DATE_TIME)));
          } else if (LocalDate.class == fieldClass){
            add(predicate, criteriaBuilder.greaterThanOrEqualTo(join.get(queryParam.getFieldName()), LocalDate.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE)));
            add(predicate, criteriaBuilder.lessThanOrEqualTo(join.get(queryParam.getFieldName()), LocalDate.parse(queryParam.getFieldValueSecondary(), DateTimeFormatter.ISO_DATE)));
          } else {
            add(predicate, criteriaBuilder.greaterThanOrEqualTo(join.get(queryParam.getFieldName()), queryParam.getFieldValue()));
            add(predicate, criteriaBuilder.lessThanOrEqualTo(join.get(queryParam.getFieldName()), queryParam.getFieldValueSecondary()));
          }

        } else {

          Class fieldClass = this.getFieldClass(queryParam.getFieldName(), clazz);

          if(LocalDateTime.class == fieldClass) {
            add(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(queryParam.getFieldName()), LocalDateTime.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE_TIME)));
            add(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(queryParam.getFieldName()), LocalDateTime.parse(queryParam.getFieldValueSecondary(), DateTimeFormatter.ISO_DATE_TIME)));
          } else if (LocalDate.class == fieldClass){
            add(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(queryParam.getFieldName()), LocalDate.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE)));
            add(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(queryParam.getFieldName()), LocalDate.parse(queryParam.getFieldValueSecondary(), DateTimeFormatter.ISO_DATE)));
          } else {
            add(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(queryParam.getFieldName()), queryParam.getFieldValue()));
            add(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(queryParam.getFieldName()), queryParam.getFieldValueSecondary()));
          }

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
