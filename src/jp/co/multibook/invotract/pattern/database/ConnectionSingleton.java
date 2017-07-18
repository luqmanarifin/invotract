package jp.co.multibook.invotract.pattern.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class ConnectionSingleton extends Executor {

  private static String url = "jdbc:sqlite:/var/www/html/phpliteadmin/invotract.db";

  public ConnectionSingleton() {

  }

  private static Connection connection = null;

  public static Connection getConnection() {
    try {
      if (connection == null) {
        DriverManager.registerDriver(new org.sqlite.JDBC());
        connection = DriverManager.getConnection(url);
        setupDatabase();
      } else if (connection.isClosed()) {
        connection = DriverManager.getConnection(url);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

  private static void setupDatabase() {
    System.out.println("setuping database, creating tables");
    createDateTable();
    createCompanyTable();
    createTaxTable();
    createRowTable();
    createKeywordTable();
    System.out.println();
  }

  private static void createTable(String name) {
    String sql = "CREATE TABLE IF NOT EXISTS " + name + " (\n"
      + "	id integer PRIMARY KEY,\n"
      + "	filePath varchar(1000),\n"
      + "	sentences text NOT NULL\n"
      + ");";
    executeQuery(sql);
    System.out.println(name + " table created!");
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
