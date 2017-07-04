package jp.co.multibook.invotract.pattern.model;

import jp.co.multibook.invotract.pattern.database.Executor;
import jp.co.multibook.invotract.pdf2sentence.Sentence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class Pattern extends Executor {

  private long id;
  private String filePath;
  private String sentences;

  public Pattern() {
  }

  public Pattern(long id, String filePath, String sentences) {
    this.id = id;
    this.filePath = filePath;
    this.sentences = sentences;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getSentences() {
    return sentences;
  }

  public void setSentences(String sentences) {
    this.sentences = sentences;
  }

  public List<Pattern> getPatterns(String table) {
    String sql = "SELECT * FROM " + table;
    ResultSet resultSet = this.getResultQuery(sql);
    List<Pattern> patterns = new ArrayList<>();
    try {
      while (resultSet.next()) {
        long id = resultSet.getInt("id");
        String filePath = resultSet.getString("filePath");
        String sentences = resultSet.getString("sentences");
        Pattern pattern = new Pattern(id, filePath, sentences);
        patterns.add(pattern);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return patterns;
  }

  public List<Pattern> getDatePatterns() {
    return getPatterns("date");
  }

  public List<Pattern> getCompanyPatterns() {
    return getPatterns("company");
  }

  public List<Pattern> getTaxPatterns() {
    return getPatterns("tax");
  }

  public List<Pattern> getRowPatterns() {
    return getPatterns("row");
  }

  public List<Pattern> getKeywordPatterns() {
    return getPatterns("keyword");
  }
}
