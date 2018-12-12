package br.com.qisi.skeleton.utils.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR)
public class CannotConvertFieldType extends RuntimeException {
  private static final long serialVersionUID = -5855032301879136153L;

  public CannotConvertFieldType(String s) {
    super(s);
  }
}
