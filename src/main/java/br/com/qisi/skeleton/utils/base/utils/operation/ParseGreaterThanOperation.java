package br.com.qisi.skeleton.utils.base.utils.operation;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import java.util.List;
import java.util.Objects;

public class ParseGreaterThanOperation implements BaseQueryParser {

  private static final String GREATER_THAN_OPERATION = ">";
  private final String query;
  private final BaseQueryParser nextQueryParser;

  public ParseGreaterThanOperation(String query, BaseQueryParser nextQueryParser) {
    this.query = query;
    this.nextQueryParser = nextQueryParser;
  }

  @Override
  public Boolean isValidExpression(String expression) {
    return expression.matches(".*[^%]>[^=].*");
  }

  @Override
  public List<QueryParam> parseQuery(List<QueryParam> queryParams) {

    queryParams = checkNullList(queryParams);

    queryParams = decorateList(queryParams, GREATER_THAN_OPERATION, GREATER_THAN_OPERATION, this.query);

    if (!Objects.isNull(nextQueryParser)) {
      return nextQueryParser.parseQuery(queryParams);
    }

    return queryParams;
  }
}
