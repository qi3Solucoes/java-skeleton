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
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                 Thread.currentThread().getContextClassLoader().getResourceAsStream("test-db.properties")) {
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

    final List<Operation> operations = new ArrayList<>();

    for (DataTableRow row : rows.subList(1, rows.size())) {
      final Insert.Builder builder = Insert.into(tableName);
      builder.columns(columns.toArray(new String[0]));
      builder.values(row.getCells().toArray(new String[0]));
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

  private void exists(final String tableName, final DataTable data) throws SQLException,
          ClassNotFoundException {
    final List<DataTableRow> rows = data.getGherkinRows();
    final List<String> columns = rows.get(0).getCells();

    final String query = "SELECT * FROM " + tableName;

    try (PreparedStatement stmt = executeStatement(query)) {

      try (ResultSet rs = stmt.executeQuery()){

        for (DataTableRow row : rows.subList(1, rows.size())) {
          assertThat(rs.next()).isTrue();
          List<String> rowValues = row.getCells();
          for (int i = 0; i < columns.size(); i++) {

            String value = rs.getString(columns.get(i));
            if(Objects.isNull(value))
              value = "null";

            Assert.assertEquals("Coluna: "+ columns.get(i), rowValues.get(i), value);
          }
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
    this.resetTable("skeleton");
  }
}
