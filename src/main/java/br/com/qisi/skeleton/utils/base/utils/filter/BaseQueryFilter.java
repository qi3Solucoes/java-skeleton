package br.com.qisi.skeleton.utils.base.utils.filter;

import br.com.qisi.skeleton.utils.base.exception.CannotConvertFieldType;
import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public interface BaseQueryFilter<T> {
  Predicate agregatePredicate();

  default Join<T,?> doJoin(String[] splitedFieldName, Root<T> root) {
    return root.join(splitedFieldName[0]);
  }

  default Boolean needJoin(String[] splitedFieldName){
    return splitedFieldName.length > 1;
  }


  default Object getValue(List<Field> fields, QueryParam queryParam, String[] splitedFieldName, Class<?> clazz) {
    Object value;

    if (needJoin(splitedFieldName)) {
      try {
        queryParam.setFieldName(splitedFieldName[1]);
        Field declaredField = clazz.getDeclaredField(splitedFieldName[0]);
        if(declaredField.getType() == List.class){
          value = getFieldType(Arrays.asList(((Class)((ParameterizedType)declaredField.getGenericType()).getActualTypeArguments()[0]).getDeclaredFields()), queryParam);
        }else {
          value = getFieldType(Arrays.asList(declaredField.getType().getDeclaredFields()), queryParam);
        }

      } catch (NoSuchFieldException e) {
        throw new CannotConvertFieldType("Can't convert type for: " + queryParam.getFieldName());
      }
    } else {
      value = getFieldType(fields, queryParam);
    }
    return value;
  }

  default Object getFieldType(List<Field> fields, QueryParam queryParam) {
    for (Field field: fields ) {
      if(queryParam.getFieldName().equalsIgnoreCase(field.getName())) {
        try {
          return this.toObject(Class.forName(field.getType().getName()), queryParam.getFieldValue());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
          throw new CannotConvertFieldType("Can't convert type for: " + queryParam.getFieldName());
        }
      }
    }
    throw new CannotConvertFieldType("Can't convert type for: " + queryParam.getFieldName());
  }

  default Object toObject(Class clazz, String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
    if( Byte.class == clazz ) return Byte.parseByte( value );
    if( Short.class == clazz ) return Short.parseShort( value );
    if( Integer.class == clazz ) return Integer.parseInt( value );
    if( Long.class == clazz ) return Long.parseLong( value );
    if( Float.class == clazz ) return Float.parseFloat( value );
    if( Double.class == clazz ) return Double.parseDouble( value );
    if( BigDecimal.class == clazz ) return  new BigDecimal(value);
    if( Enum.class.isAssignableFrom(clazz)) return clazz.getMethod("valueOf", String.class).invoke(null, value);
    return value;
  }

  default void add(Predicate predicate, Expression<Boolean> expression) {
    predicate.getExpressions().add(expression);
  }

  default Class getFieldClass(String fieldName, Class clazz) {
    try {

      Field declaredField = clazz.getDeclaredField(fieldName);
      return Class.forName(declaredField.getType().getName());

    } catch (NoSuchFieldException | ClassNotFoundException e) {

      try {
        Field declaredField = ((Class)clazz.getGenericSuperclass()).getDeclaredField(fieldName);
        return Class.forName(declaredField.getType().getName());
      } catch (NoSuchFieldException | ClassNotFoundException ex) {
        throw new CannotConvertFieldType("Can't convert type for: " + fieldName);
      }

    }
  }
}
