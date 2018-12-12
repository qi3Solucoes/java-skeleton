package br.com.qisi.skeleton.utils.base.utils.operation;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import java.util.List;
import java.util.Objects;

public class ParserBetweenOperation implements BaseQueryParser {

  private static final String BTW_OPERATOR = "BTW";
  private String query;
  private BaseQueryParser nextQueryParser;

  public ParserBetweenOperation(String query, BaseQueryParser nextQueryParser) {
    this.query = query;
    this.nextQueryParser = nextQueryParser;
  }

  @Override
  public Boolean isValidExpression(String expression) {
    return expression.matches(".*:\\(.* [btwBTW].*");
  }

  @Override
  public List<QueryParam> parseQuery(List<QueryParam> queryParams) {
    queryParams = checkNullList(queryParams);

    for (String expression : splitQuery(query)) {
      if (isValidExpression(expression)) {

        String[] splitedEquals = expression.split(":[(]");

        String[] splited = splitedEquals[1].split("\\W*((?i)BTW(?-i))\\W*");

        queryParams.add(QueryParam.builder()
                .fieldName(splitedEquals[0])
                .fieldValue(splited[0].replace("(", ""))
                .fieldValueSecondary(splited[1].replace(")", ""))
                .operation(BTW_OPERATOR)
                .build());
      }
    }

    if (!Objects.isNull(nextQueryParser)) {
      return nextQueryParser.parseQuery(queryParams);
    }

    return queryParams;
  }
}
