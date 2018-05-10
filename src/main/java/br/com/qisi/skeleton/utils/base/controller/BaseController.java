package br.com.qisi.skeleton.utils.base.controller;


import br.com.qisi.skeleton.utils.base.model.BaseModel;
import br.com.qisi.skeleton.utils.base.service.BaseService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
@CrossOrigin
public class BaseController<T extends BaseModel>{

  @Autowired
  private BaseService<T> baseService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<T> findPages(Pageable pageable){
    log.debug("get all request");
    return this.baseService.findPage(pageable);
  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public T findOne(T t) {
    log.debug("get request -> id: " + t.getId() + t.getClass().getName());
    return this.baseService.findByExample(t);
  }

  @GetMapping("/filtros")
  @ResponseStatus(HttpStatus.OK)
  public Page<T> findByExample(T t, Pageable pageable){
    return this.baseService.findPageByExample(pageable, t);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public T save(@Valid @RequestBody T t){
    log.debug("Post request: " + t.getClass().getName());
    return this.baseService.doPost(t);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void putRequest(@Valid T t){
    log.debug("Put request: " + t.getClass().getName());
    this.baseService.saveOrUpdate(t);
  }

  @PatchMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public T patchRequest(@NonNull @PathVariable("id") Long id, @RequestBody @Valid T t){
    t.setId(id);
    return this.baseService.pathNonNull(t);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(T t){
    log.debug("Delete request: " + t.getClass().getName());
    this.baseService.delete(t);
  }

}
