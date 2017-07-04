package jp.co.multibook.invotract.pattern.database;

import java.sql.*;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class Executor {

  public Executor() {

  }

  public void executeQuery(String query) {
    try {
      PreparedStatement preparedStatement = ConnectionSingleton.getConnection().prepareStatement(query);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public ResultSet getResultQuery(String query) {
    try (Connection conn = ConnectionSingleton.getConnection();
         Statement stmt  = conn.createStatement();
         ResultSet rs    = stmt.executeQuery(query)){
      return rs;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

}
