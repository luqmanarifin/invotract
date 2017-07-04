package jp.co.multibook.invotract.pattern.model;

import jp.co.multibook.invotract.pattern.database.Executor;

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

}
