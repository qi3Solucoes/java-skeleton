package br.com.qisi.skeleton.utils.base.repository;

import br.com.qisi.skeleton.utils.base.model.BaseModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

@NoRepositoryBean
public interface BaseRepository<T extends BaseModel> extends Repository<T, Long>,
                                                             QueryByExampleExecutor<T>,
                                                             JpaSpecificationExecutor<T>,
                                                             PagingAndSortingRepository<T, Long> {
}
