package br.com.qisi.skeleton.skeleton.model;


import br.com.qisi.skeleton.utils.base.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table()
@Entity
@Data
public class Skeleton extends BaseModel {

  @Id
  private Long id;

}
