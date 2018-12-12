package br.com.qisi.skeleton.utils.base.repository;

import br.com.qisi.skeleton.utils.base.model.BaseModel;
import br.com.qisi.skeleton.utils.base.utils.filter.*;
import br.com.qisi.skeleton.utils.specification.BaseSpecification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class FilterSpecification<T extends BaseModel> implements BaseSpecification<T> {

  private List<QueryParam> queryParams;
  private Class<T> clazz;

  public FilterSpecification(List<QueryParam> queryParams, Class<T> clazz) {
    this.queryParams = queryParams;
    this.clazz = clazz;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Predicate predicate = criteriaBuilder.conjunction();

    return new EqualsFilter<>(predicate, criteriaBuilder, root, queryParams, clazz,
                new GreaterThanFilter<>(predicate, criteriaBuilder, root, queryParams, clazz,
                    new LessThanFilter<>(predicate, criteriaBuilder, root, queryParams, clazz,
                            new GreaterThanOrEqualsFilter<>(predicate, criteriaBuilder, root, queryParams, clazz,
                                    new LessThanOrEqualsFilter<>(predicate, criteriaBuilder, root, queryParams, clazz,
                                            new FullLikeFilter<>(predicate, criteriaBuilder, root, queryParams,
                                                    new RightLikeFilter<>(predicate, criteriaBuilder, root, queryParams,
                                                            new LeftLikeFilter<>(predicate, criteriaBuilder, root, queryParams,
                                                                    new OrFilter<>(predicate, criteriaBuilder, root, queryParams,
                                                                            new BetweenFilter<>(predicate, criteriaBuilder, root, queryParams, clazz,
                                                                                    new InFilter<>(predicate, criteriaBuilder, root, queryParams, null)))))))))))
    .agregatePredicate();
  }

}
