package br.com.qisi.skeleton.utils.base.utils.operation;

import br.com.qisi.skeleton.utils.base.exception.CannotConvertFieldType;
import br.com.qisi.skeleton.utils.base.repository.QueryParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ParserInOperation implements BaseQueryParser {

  private static final String IN_OPERAPOR = ":[";
  private final String query;
  private final BaseQueryParser nextQueryParser;

  public ParserInOperation(String query, BaseQueryParser nextQueryParser) {
    this.query = query;
    this.nextQueryParser = nextQueryParser;
  }

  @Override
  public Boolean isValidExpression(String expression) {
    return expression.matches("^.*:\\[.*$");
  }

  @Override
  public List<QueryParam> parseQuery(List<QueryParam> queryParams) {

    queryParams = checkNullList(queryParams);

    for (String expression : splitQuery(query)) {
      if (isValidExpression(expression)) {

        String[] splitedEquals = expression.split(":");

        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();

        try {
          List<String> inValues = objectMapper.readValue(splitedEquals[1], typeFactory.constructCollectionType(List.class, String.class));

          queryParams.add(QueryParam.builder()
                  .fieldName(splitedEquals[0])
                  .operation(IN_OPERAPOR)
                  .inValues(inValues)
                  .build());

        } catch (IOException e) {
          throw new CannotConvertFieldType("Can't convert in values, plz verify");
        }
      }
    }

    if (!Objects.isNull(nextQueryParser)) {
      return nextQueryParser.parseQuery(queryParams);
    }

    return queryParams;
  }
}
