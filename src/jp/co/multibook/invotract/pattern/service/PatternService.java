package jp.co.multibook.invotract.pattern.service;

import jp.co.multibook.invotract.pattern.database.ConnectionSingleton;
import jp.co.multibook.invotract.pattern.database.Executor;
import jp.co.multibook.invotract.pattern.model.Instance;
import jp.co.multibook.invotract.pattern.model.Pattern;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by luqmanarifin on 7/4/17.
 */
public class PatternService extends Executor {

  public static Pattern getPattern(String table, long id) {
    Pattern pattern = null;
    String sql = "SELECT * FROM " + table + " WHERE id='" + id + "';";
    try (Connection conn         = ConnectionSingleton.getConnection();
         Statement stmt         = conn.createStatement();
         ResultSet resultSet    = stmt.executeQuery(sql)){

      while (resultSet.next()) {
        id = resultSet.getInt("id");
        String filePath = resultSet.getString("filePath");
        String sentences = resultSet.getString("sentences");
        pattern = new Pattern(id, filePath, sentences);
      }

      stmt.close();
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return pattern;
  }

  public static List<Pattern> getPatterns(String table) {
    List<Pattern> patterns = new ArrayList<>();
    String sql = "SELECT * FROM " + table + ";";
    try (Connection conn         = ConnectionSingleton.getConnection();
         Statement stmt         = conn.createStatement();
         ResultSet resultSet    = stmt.executeQuery(sql)){

      while (resultSet.next()) {
        long id = resultSet.getInt("id");
        String filePath = resultSet.getString("filePath");
        String sentences = resultSet.getString("sentences");
        Pattern pattern = new Pattern(id, filePath, sentences);
        patterns.add(pattern);
      }

      stmt.close();
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return patterns;
  }

  /**
   * ARFF file -> list of instance
   * @param text
   * @return
   */
  public static List<Instance> convertTextToInstanceList(String text) {
    List<Instance> instances = new ArrayList<>();
    Scanner scanner = new Scanner(text);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (!line.startsWith("@")) {
        Instance instance = new Instance();
        instance.inject(line);
        instances.add(instance);
      }
    }
    return instances;
  }

  /**
   * list of instance -> ARFF file
   * @param instances
   * @return
   */
  public static String convertInstanceListToText(List<Instance> instances) {
    String text = "@RELATION invoices\n@ATTRIBUTE x REAL\n@ATTRIBUTE y REAL\n@ATTRIBUTE size REAL\n@ATTRIBUTE class {yes, no}\n@DATA\n";
    for (Instance instance : instances) {
      text += instance.toString() + "\n";
    }
    return text;
  }

  public static List<Pattern> getDatePatterns() {
    return getPatterns("date");
  }

  public static List<Pattern> getCompanyPatterns() {
    return getPatterns("company");
  }

  public static List<Pattern> getTaxPatterns() {
    return getPatterns("tax");
  }

  public static List<Pattern> getRowPatterns() {
    return getPatterns("row");
  }

  public static List<Pattern> getKeywordPatterns() {
    return getPatterns("keyword");
  }

  /**
   *
   * @param source null if insert, otherwise it's a pattern source to follow
   * @param instances
   */
  public static void addPattern(String table, Pattern source, List<Instance> instances, String pdfPath) {
    if (source == null) {
      insertPattern(table, instances, pdfPath);
    } else {
      updatePattern(table, source, instances, pdfPath);
    }
  }

  private static void insertPattern(String table, List<Instance> instances, String pdfPath) {
    String sentences = convertInstanceListToText(instances);
    String sql = "INSERT INTO \"" + table + "\" (\"id\", \"filePath\", \"sentences\") " +
                  "VALUES (NULL, '" + pdfPath + "', '" + sentences + "')";
    executeQuery(sql);
  }

  private static void updatePattern(String table, Pattern source, List<Instance> instances, String pdfPath) {
    String sentences = source.getSentences();
    for (Instance instance : instances) {
      sentences += instance.toString() + "\n";
    }
    String sql = "UPDATE \"" + table + "\"\n" +
      "SET \"sentences\" = '" + sentences + "'\n" +
      "WHERE \"id\" = " + source.getId() + "";
    executeQuery(sql);
  }
}
