package br.com.qisi.skeleton.utils.specification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Objects;

public interface BaseSpecification<T> extends Specification<T> {

  default void doLike(String fieldValue, String fieldName, Predicate predicate, Join<T, ?> root, CriteriaBuilder builder) {
    if(StringUtils.isNotEmpty(fieldValue))
      add(predicate, builder.like(builder.lower(root.get(fieldName)), "%" + fieldValue.toLowerCase() + "%"));
  }

  default void doLike(String fieldValue, String fieldName, Predicate predicate, Root<T> root, CriteriaBuilder builder) {
    if(StringUtils.isNotEmpty(fieldValue))
      add(predicate, builder.like(builder.lower(root.get(fieldName)), "%" + fieldValue.toLowerCase() + "%"));
  }

  default void doEquals(Boolean fieldValue, String fieldName, Predicate predicate, Root<T> root, CriteriaBuilder builder){
    if(Objects.nonNull(fieldValue))
      add(predicate, builder.equal(root.get(fieldName), fieldValue));
  }

  default void doEquals(String fieldValue, String fieldName, Predicate predicate, Join<T, ?> root, CriteriaBuilder builder){
    if(Objects.nonNull(fieldValue))
      add(predicate, builder.equal(root.get(fieldName), fieldValue));
  }

  default void doEquals(String fieldValue, String fieldName, Predicate predicate, Root<T> root, CriteriaBuilder builder){
    if(Objects.nonNull(fieldValue))
      add(predicate, builder.equal(root.get(fieldName), fieldValue));
  }

  default void doEquals(Long fieldValue, String fieldName, Predicate predicate, Join<T, ?> root, CriteriaBuilder builder){
    if(Objects.nonNull(fieldValue))
      add(predicate, builder.equal(root.get(fieldName), fieldValue));
  }

  default void doEqualsIgnoreCase(String fieldValue, String fieldName, Predicate predicate, Join<T, ?> root, CriteriaBuilder builder){
    if(Objects.nonNull(fieldValue))
      add(predicate, builder.equal(builder.lower(root.get(fieldName)), fieldValue.toLowerCase()));
  }

  default void isNull(String fildName, Predicate predicate, Root<T> root, CriteriaBuilder builder){
    add(predicate, builder.isNull(root.get(fildName)));
  }

  default void add(Predicate predicate, Expression<Boolean> expression) {
    predicate.getExpressions().add(expression);
  }


}
