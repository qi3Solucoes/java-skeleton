package br.com.qisi.skeleton.utils.base.repository;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QueryParam {
  private String fieldName;
  private String operation;
  private String fieldValue;
  private String fieldValueSecondary;
  private List<String> inValues;
}
