package jp.co.multibook.invotract.pattern_distinguisher.database;

import java.sql.Connection;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class ConnectionSingleton {

  public ConnectionSingleton() {

  }

  private static Connection connection = null;

  public Connection getConnection() {
    if (connection == null) {

    }
    return connection;
  }

  private void setupDatabase() {

  }
}
