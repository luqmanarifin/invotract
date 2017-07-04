package jp.co.multibook.invotract.pattern.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class Executor {

  public Executor() {

  }

  public static void executeQuery(String query) {
    try {
      PreparedStatement preparedStatement = ConnectionSingleton.getConnection().prepareStatement(query);
      preparedStatement.executeUpdate();
      preparedStatement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

}
