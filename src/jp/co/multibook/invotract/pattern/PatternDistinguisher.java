package jp.co.multibook.invotract.pattern;

import jp.co.multibook.invotract.pattern.model.Pattern;

import java.util.List;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class PatternDistinguisher {

  public PatternDistinguisher() {
    System.out.println("trying to connect to DB");
    List<Pattern> patterns = new Pattern().getDatePatterns();
    for (Pattern pattern : patterns) {
      System.out.println(pattern.getId());
      System.out.println(pattern.getFilePath());
      System.out.println(pattern.getSentences());
      System.out.println();
    }
  }
}
