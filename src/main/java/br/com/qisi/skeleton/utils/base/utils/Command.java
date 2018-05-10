package br.com.qisi.skeleton.utils.base.utils;

public interface Command<T> {
  T execute(T t);
}
