package br.com.qisi.skeleton.utils.base.utils.operation;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface BaseQueryParser {
  Boolean isValidExpression(String expression);
  List<QueryParam> parseQuery(List<QueryParam> queryParams);

  default List<String> splitQuery(String query){
    return Arrays.asList(query.split("[|]"));
  }

  default List<QueryParam> checkNullList(List<QueryParam> queryParams){
    if(Objects.isNull(queryParams)){
      return new ArrayList<>();
    }
    return queryParams;
  }

  default List<QueryParam> decorateList(List<QueryParam> queryParams, String operator, String splitRegex, String query){
    for (String expression : splitQuery(query)) {
      if (isValidExpression(expression)) {
        String[] splited = expression.split(splitRegex);
        queryParams.add(QueryParam.builder()
                .fieldName(splited[0])
                .fieldValue(splited.length > 1 ? splited[1] : "")
                .operation(operator)
                .build());
      }
    }
    return queryParams;
  }
}
