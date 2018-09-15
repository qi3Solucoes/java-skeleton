package br.com.qisi.skeleton.integration.steps;

import com.google.common.io.Resources;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;
import com.ninja_squad.dbsetup.operation.SqlOperation;
import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import gherkin.formatter.model.DataTableRow;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.google.common.truth.Truth.assertThat;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

@Slf4j
public class DatabaseSteps {
  private static final Properties properties = new Properties();

  static {
    try (InputStream resource =
                 Thread.currentThread().getContextClassLoader().getResourceAsStream("test-db.yaml")) {
      properties.load(resource);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private final Destination destination = new DriverManagerDestination(
          properties.getProperty("database.url"), properties.getProperty("database.user"),
          properties.getProperty("database.password"));

  @Given("^I have the following rows in the \"(.*?)\" table:$")
  public void iHaveTheFollowingRowsInTheTable(final String tableName, final DataTable data){
    this.insert(tableName, data);
  }

  @Given("^I have only the following rows in the \"([^\"]*)\" table:$")
  public void iHaveOnlyTheFollowingRowsInTheTable(final String tableName,
                                                  final DataTable data) {
    this.deleteAll(tableName);
    this.insert(tableName, data);
  }

  @Given("^I have no rows in the \"([^\"]*)\" table$")
  public void iHaveNoRowsInTheTable(final String tableName){
    this.deleteAll(tableName);
  }

  private void insert(final String tableName, final DataTable data) {
    final List<DataTableRow> rows = data.getGherkinRows();
    final List<String> columns = rows.get(0).getCells();

    boolean needCreatedDate = Boolean.FALSE;
    boolean needModifiedDate = Boolean.FALSE;

    if(!columns.contains("data_criacao")){
      columns.add("data_criacao");
      needCreatedDate = Boolean.TRUE;
    }

    if(!columns.contains("data_alteracao")){
      columns.add("data_alteracao");
      needModifiedDate = Boolean.TRUE;
    }


    final List<Operation> operations = new ArrayList<>();

    for (DataTableRow row : rows.subList(1, rows.size())) {
      final Insert.Builder builder = Insert.into(tableName);
      List<String> cells = row.getCells();

      //data_criacao
      if(needCreatedDate)
        cells.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

      //data_alteracao
      if(needModifiedDate)
        cells.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

      //builder.values(cells.toArray(new String[0]));
      Insert.RowBuilder rowBuilder = builder.row();
      for (int i = 0; i < cells.size(); i++) {
        if(!("null".equals(cells.get(i)) || "NULL".equals(cells.get(i))))
          rowBuilder.column(columns.get(i), cells.get(i));
      }
       rowBuilder.end();

      operations.add(builder.build());
    }

    this.apply(sequenceOf(operations));
  }

  private void deleteAll(final String tableName) {
    this.apply(deleteAllFrom(tableName));
  }

  private void apply(final Operation operation) {
    new DbSetup(destination, operation).launch();
  }

  @Given("^I have the following sql script \"([^\"]*)\"$")
  public void iHaveTheFollowingSQLScript(final String script) throws SQLException, FileNotFoundException {
    new ScriptRunner(this.destination.getConnection()).runScript(new BufferedReader(new FileReader(Resources.getResource(
            script).getPath())));
  }

  @Then("^I should have the following rows in the \"([^\"]*)\" table:$")
  public void iShouldHaveTheFollowingRowsInTheTable(final String tableName,
                                                    final DataTable data) throws SQLException, ClassNotFoundException {
    exists(tableName, data);
  }

  @Then("^I should have the following rows in any order in the \"([^\"]*)\" table:$")
  public void iShouldHaveTheFollowingRowsInAnyOrderInTheTable(final String tableName,
                                                    final DataTable data) throws SQLException, ClassNotFoundException, JSONException {
    existsInAny(tableName, data);
  }

  private void exists(final String tableName, final DataTable data) throws SQLException,
          ClassNotFoundException {
    final List<DataTableRow> rows = data.getGherkinRows();
    final List<String> columns = rows.get(0).getCells();

    final String query = "SELECT * FROM " + tableName;

    try (PreparedStatement stmt = executeStatement(query)) {

      try(ResultSet resultSet = stmt.executeQuery()){
        for (DataTableRow row : rows.subList(1, rows.size())) {
          assertThat(resultSet.next()).isTrue();
          List<String> rowValues = row.getCells();
          for (int i = 0; i < columns.size(); i++) {

            String value = resultSet.getString(columns.get(i));
            if(Objects.isNull(value))
              value = "null";

            Assert.assertEquals("Coluna: "+ columns.get(i), rowValues.get(i), value);
          }
        }
      }
    }
  }

  private void existsInAny(final String tableName, final DataTable data) throws SQLException, ClassNotFoundException, JSONException {

    final String query = "SELECT * FROM " + tableName;

    try (PreparedStatement stmt = executeStatement(query)) {

      try(ResultSet resultSet = stmt.executeQuery()){
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        final List<DataTableRow> rows = data.getGherkinRows();
        final List<String> columns = rows.get(0).getCells();

        JSONArray json = new JSONArray();

        while(resultSet.next()) {
          int numColumns = resultSetMetaData.getColumnCount();
          JSONObject obj = new JSONObject();
          for (int i=1; i<=numColumns; i++) {
            String column_name = resultSetMetaData.getColumnName(i);
            obj.put(column_name, Objects.isNull(resultSet.getObject(column_name))? "null" : resultSet.getObject(column_name).toString());
          }
          json.put(obj);
        }

        Boolean hasInAnyOrder;
        for (DataTableRow row : rows.subList(1, rows.size())) {
          hasInAnyOrder = Boolean.FALSE;

          List<String> rowValues = row.getCells();

          for(int i=0; i < json.length(); i++){
            String jsonString = json.get(i).toString().toLowerCase();

            for (int j = 0; j < columns.size(); j++) {

              String contentAsJson =  "\""+columns.get(j).toLowerCase()+ "\":\"" + rowValues.get(j).toLowerCase()+ "\"";
              hasInAnyOrder = jsonString.contains(contentAsJson);
              //if column value not exists on json content, break the loop and go to next json line
              if(!hasInAnyOrder)
                break;
            }

            //if founded line in json content go to next table line assert.
            if(hasInAnyOrder)
              break;
          }
          Assert.assertEquals("expected line does not found on table content \n" +
                                        columns + "\n" + row.getCells() +
                                       "table content as jason: " + json.toString(2) ,
                                        Boolean.TRUE,
                                        hasInAnyOrder);
        }
      }
    }

  }

  private void resetAllH2Sequences() throws SQLException, ClassNotFoundException {
    final String query = "SELECT * FROM INFORMATION_SCHEMA.SEQUENCES";
    try (PreparedStatement stmt = executeStatement(query)) {
      try(final ResultSet rs = stmt.executeQuery()){
        while (rs.next()){
          String sequenceName = rs.getString("SEQUENCE_NAME");
          this.apply(SqlOperation.of("ALTER SEQUENCE "+ sequenceName + " RESTART WITH 1;"));
        }
      }
    }

  }

  private PreparedStatement executeStatement(String query) throws SQLException, ClassNotFoundException {
    Connection conn = this.getConnection();
    return conn.prepareStatement(query);
  }

  private Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName(properties.getProperty("database.driver"));
    return DriverManager.getConnection(properties.getProperty("database.url"),
            properties.getProperty("database.user"),
            properties.getProperty("database.password"));
  }

  private void resetTable(String tableName){
    this.deleteAll(tableName);
  }

  @Before
  public void cleanDb() throws SQLException, ClassNotFoundException {
    this.resetAllH2Sequences();
  }
}
