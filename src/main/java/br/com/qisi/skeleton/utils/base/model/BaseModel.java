package br.com.qisi.skeleton.utils.base.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseModel implements Persistable<Long> {

  @Override
  public abstract Long getId();

  public abstract void setId(Long id);

  @Column(name = "created_date")
  @CreatedDate
  @JsonSerialize(using = ToStringSerializer.class)
  private LocalDateTime createdDate;

  @Column(name = "last_modified_date")
  @LastModifiedDate
  @JsonSerialize(using = ToStringSerializer.class)
  private LocalDateTime lastModifiedDate;

  @Override
  @JsonIgnore
  public boolean isNew() {
    return Objects.isNull(this.getId());
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public LocalDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }
}
