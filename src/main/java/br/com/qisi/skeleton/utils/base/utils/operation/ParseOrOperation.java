package br.com.qisi.skeleton.utils.base.utils.operation;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import java.util.List;
import java.util.Objects;

public class ParseOrOperation implements BaseQueryParser {

  private static final String OR_OPERATOR = "OR";
  private final String query;
  private final BaseQueryParser nextQueryParser;

  public ParseOrOperation(String query, BaseQueryParser nextQueryParser) {
    this.query = query;
    this.nextQueryParser = nextQueryParser;
  }

  @Override
  public Boolean isValidExpression(String expression) {
    return expression.matches(".*:\\(.* [orOr].*");
  }

  @Override
  public List<QueryParam> parseQuery(List<QueryParam> queryParams) {
    queryParams = checkNullList(queryParams);

    for (String expression : splitQuery(query)) {
      if (isValidExpression(expression)) {

        String[] splitedEquals = expression.split(":");

        String[] splited = splitedEquals[1].split("\\W*((?i)OR(?-i))\\W*");

        queryParams.add(QueryParam.builder()
                .fieldName(splitedEquals[0])
                .fieldValue(splited[0].replace("(", ""))
                .fieldValueSecondary(splited[1].replace(")", ""))
                .operation(OR_OPERATOR)
                .build());
      }
    }

    if (!Objects.isNull(nextQueryParser)) {
      return nextQueryParser.parseQuery(queryParams);
    }

    return queryParams;
  }
}
