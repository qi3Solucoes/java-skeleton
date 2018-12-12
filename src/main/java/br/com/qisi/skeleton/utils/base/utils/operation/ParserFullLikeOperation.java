package br.com.qisi.skeleton.utils.base.utils.operation;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import java.util.List;
import java.util.Objects;

public class ParserFullLikeOperation implements BaseQueryParser {
  private static final String FULL_LIKE_OPERATION = "%";
  private final String query;
  private final BaseQueryParser nextParser;

  public ParserFullLikeOperation(String query, BaseQueryParser nextParser) {
    this.query = query;
    this.nextParser = nextParser;
  }

  @Override
  public Boolean isValidExpression(String expression) {
    return expression.matches(".*%[^><].*");
  }

  @Override
  public List<QueryParam> parseQuery(List<QueryParam> queryParams) {

    queryParams = checkNullList(queryParams);

    queryParams = decorateList(queryParams, FULL_LIKE_OPERATION, FULL_LIKE_OPERATION, this.query);

    if (!Objects.isNull(nextParser)) {
      return nextParser.parseQuery(queryParams);
    }

    return queryParams;
  }
}
