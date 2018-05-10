package br.com.qisi.skeleton.utils.base.service;

import br.com.qisi.skeleton.utils.BeansUtil;
import br.com.qisi.skeleton.utils.base.model.BaseModel;
import br.com.qisi.skeleton.utils.base.repository.BaseRepository;
import br.com.qisi.skeleton.utils.base.utils.Command;
import br.com.qisi.skeleton.utils.exception.NotFoundException;
import br.com.qisi.skeleton.utils.specification.BaseSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(rollbackOn = Exception.class)
public class BaseService<T extends BaseModel>{

  @Autowired
  protected BaseRepository<T> baseRepository;


  public T findByExample(T t) {
    return this.baseRepository.findOne(Example.of(t)).orElseThrow(NotFoundException::new);
  }

  public Iterable<T> findAllByExample(T t){
    return Optional.ofNullable(this.baseRepository.findAll(Example.of(t))).orElseThrow(NotFoundException::new);
  }

  public List<T> findAllBySpecification(BaseSpecification<T> vaptuberSpecification){
    return this.baseRepository.findAll(vaptuberSpecification);
  }


  public Page<T> findPageBySpecification(BaseSpecification<T> vaptuberSpecification, Pageable pageable){
    return this.baseRepository.findAll(vaptuberSpecification, pageable);
  }

  public Page<T> findPage(Pageable pageable) {
    log.debug("Busca Paginada BaseService");
    return this.baseRepository.findAll(pageable);
  }

  public Page<T> findPageByExample(Pageable pageable, T t){
    log.debug("Busca Paginada coms example");
    return this.baseRepository.findAll(Example.of(t), pageable);
  }

  public T doPost(T t){
    t = this.beforePost().execute(t);
    T salva = save(t);
    return this.afterPost().execute(salva);
  }

  private T save(T t) {
    log.debug("salvar BaseService");
    t.setId(null);
    return this.baseRepository.save(t);
  }

  public void delete(T t) {
    log.debug("delete BaseService");
    T tPersistido = this.findByExample(t);
    this.baseRepository.delete(tPersistido);
  }

  public T saveOrUpdate(T t){
    log.debug("save ou atualiza BaseService");
    return this.baseRepository.save(t);
  }

  public T pathNonNull(T t) {
    log.debug("pathNonNull BaseService");
    T tPersistido = this.findByExample(t);
    T tAtualizado = new BeansUtil<T>().copyNonNullProperties(t, tPersistido);
    return this.save(tAtualizado);
  }

  protected Command<T> beforePost(){
    log.debug("beforePost without implementation ");
    return t-> t;
  }

  protected Command<T> afterPost(){
    log.debug("afterPost without implementation ");
    return t-> t;
  }
}

