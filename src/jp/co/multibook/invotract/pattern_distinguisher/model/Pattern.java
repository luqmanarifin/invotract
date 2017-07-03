package jp.co.multibook.invotract.pattern_distinguisher.model;

import jp.co.multibook.invotract.pdf2sentence.Sentence;

import java.util.List;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class Pattern {

  private long id;
  private List<Sentence> sentences;

  public Pattern(long id, List<Sentence> sentences) {
    this.id = id;
    this.sentences = sentences;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Sentence> getSentences() {
    return sentences;
  }

  public void setSentences(List<Sentence> sentences) {
    this.sentences = sentences;
  }

  public List<Pattern> getPatterns(String table) {
    return null;
  }

  public List<Pattern> getDatePatterns() {
    return null;
  }

  public List<Pattern> getCompanyPatterns() {
    return null;
  }

  public List<Pattern> getTaxPatterns() {
    return null;
  }

  public List<Pattern> getRowPatterns() {
    return null;
  }

  public List<Pattern> getKeywordPatterns() {
    return null;
  }
}
