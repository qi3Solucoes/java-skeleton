package br.com.qisi.skeleton.utils.base.utils.operation;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import java.util.List;
import java.util.Objects;

public class ParseEqualsOperation implements BaseQueryParser {

  private static final String EQUALS_OPERATOR = ":";
  private final String query;
  private final BaseQueryParser nextQueryParser;

  public ParseEqualsOperation(String query, BaseQueryParser nextQueryParser) {
    this.query = query;
    this.nextQueryParser = nextQueryParser;
  }

  @Override
  public Boolean isValidExpression(String expression) {
    return expression.matches("^(?=.*?\\b:\\b)((?!\\().)*$") && expression.matches("[^:]+:[^:]+");
  }

  @Override
  public List<QueryParam> parseQuery(List<QueryParam> queryParams) {
    queryParams = checkNullList(queryParams);

    queryParams = decorateList(queryParams, EQUALS_OPERATOR, EQUALS_OPERATOR, this.query);

    if (!Objects.isNull(nextQueryParser)) {
      return nextQueryParser.parseQuery(queryParams);
    }

    return queryParams;
  }
}
