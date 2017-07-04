package jp.co.multibook.invotract.pattern.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class ConnectionSingleton {

  public ConnectionSingleton() {

  }

  private static Connection connection = null;

  public static Connection getConnection() {
    if (connection == null) {
      String url = "jdbc:sqlite:invotract.db";
      try {
        connection = DriverManager.getConnection(url);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      setupDatabase();
    }
    return connection;
  }

  private static void setupDatabase() {
    createDateTable();
    createCompanyTable();
    createTaxTable();
    createRowTable();
    createKeywordTable();
  }

  private static void createTable(String name) {
    String sql = "CREATE TABLE IF NOT EXISTS " + name + " (\n"
      + "	id integer PRIMARY KEY,\n"
      + "	filePath varchar(1000),\n"
      + "	sentences text NOT NULL\n"
      + ");";

  }

  private static void createDateTable() {
    createTable("date");
  }

  private static void createCompanyTable() {
    createTable("company");
  }

  private static void createTaxTable() {
    createTable("tax");
  }

  private static void createRowTable() {
    createTable("row");
  }

  private static void createKeywordTable() {
    createTable("keyword");
  }
}
