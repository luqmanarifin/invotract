package jp.co.multibook.invotract.pattern;

import jp.co.multibook.invotract.pattern.model.Instance;
import jp.co.multibook.invotract.pattern.model.Pattern;
import jp.co.multibook.invotract.pattern.service.PatternService;
import jp.co.multibook.invotract.pattern.service.WekaService;
import jp.co.multibook.invotract.pdf2sentence.Sentence;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class PatternDistinguisher {

  private final static double similarPercentage = 85; // minimum value of "recall", so two patterns can be considered similar
  private final static double fontSizeError = 0.2;    // tolerance of font size difference
  private final static int keywordLimit = 10;         // limit for decision -> using geometry/ML approach
  public final static String[] keywords = {
    "invoice",
    "bill",
    "date",
    "qty",
    "description",
    "price",
    "payment",
    "currency",
    "quantity",
    "item",
    "unit"
  };

  public PatternDistinguisher () throws Exception {
    //System.out.println("testing DB connection...");
    List<Pattern> patterns = PatternService.getDatePatterns();
    if (!patterns.isEmpty()) {
      //System.out.println("DB connected!");
    }
    //System.out.println();
  }

  /**
   *
   * @param document
   * @return null, if this PDF file does not similar to any file
   *         pattern, the most similar PDF file with this file
   */
  public Pattern getSimilarPattern(List<Sentence> document) {
    List<Pattern> patterns = PatternService.getKeywordPatterns();
    boolean[] isKeyword = new boolean[document.size()];
    //System.out.println("KEYWORD OF DOCUMENT:");
    for (int i = 0; i < document.size(); i++) {
      for (int j = 0; j < keywords.length; j++) {
        isKeyword[i] = StringUtils.containsIgnoreCase(document.get(i).getText(), keywords[j]);
        if (isKeyword[i]) {
          //System.out.println(document.get(i).getText());
          break;
        }
      }
    }
    //System.out.println();

    double best = -1;
    int index = -1;
    for (int i = 0; i < patterns.size(); i++) {
      double similarityValue = similarity(patterns.get(i), document, isKeyword);
      if (similarityValue > best) {
        best = similarityValue;
        index = i;
      }
      //System.out.println("similarity " + similarityValue + "% with index " + patterns.get(i).getId());
      //System.out.println();
    }
    //System.out.println("RESULT OF FINDING SIMILAR PATTERN:");
    if (index == -1 || best < similarPercentage) {
      //System.out.println("not found any similar pattern before");
      //System.out.println("create a new pattern on database");
      //System.out.println();
      return null;
    } else {
      //System.out.println("similar with pattern index: " + patterns.get(index).getId());
      //System.out.println("pdf path: " + patterns.get(index).getFilePath());
      //System.out.println("similarity percentage: " + best);
      //System.out.println();
      return patterns.get(index);
    }
  }

  private double similarity(Pattern pattern, List<Sentence> document, boolean[] isKeyword) {
    List<Instance> instances = PatternService.convertTextToInstanceList(pattern.getSentences());
    int yesClass = 0;
    for (Instance instance : instances) {
      if (instance.isClazz()) {
        yesClass++;
      }
    }
    if (yesClass < keywordLimit) {
      //System.out.println("finding similarity using geometry approach");
      return getSimilarityByGeometry(instances, document, isKeyword);
    } else {
      //System.out.println("finding similarity using machine learning approach");
      return getSimilarityByMachineLearning(instances, document);
    }
  }

  private double getSimilarityByGeometry(List<Instance> instances, List<Sentence> document, boolean[] isKeyword) {
    Collections.sort(instances, Instance.compareByY);
    int totalKeyword = 0, correctKeyword = 0;
    for (int i = 0; i < isKeyword.length; i++) {
      if (isKeyword[i]) {
        totalKeyword++;
        int l = 0, r = instances.size() - 1;
        while (l < r) {
          int mid = (l + r) / 2;
          if (instances.get(mid).getY() < document.get(i).getY() - document.get(i).getSize()) {
            l = mid + 1;
          } else {
            r = mid;
          }
        }
        //System.out.println("finding " + document.get(i).getY() + " starting from index " + l);
        boolean ada = false;
        while (l < instances.size()
          && instances.get(l).getY() <= document.get(i).getY() + document.get(i).getSize()
          && !ada) {
          if (Math.abs(instances.get(l).getSize() - document.get(i).getSize()) < fontSizeError) {
            if (instances.get(l).isClazz()) {
              double width = document.get(i).getEndX() - document.get(i).getX();
              double bigL = Math.max(instances.get(l).getX(), document.get(i).getX());
              double smallR = Math.min(instances.get(l).getX() + width, document.get(i).getEndX());
              //System.out.println(bigL + " and " + smallR);
              if (bigL <= smallR) {
                ada = true;
              }
            }
          }
          l++;
        }
        if (ada) {
          correctKeyword++;
        }
      }
    }
    //System.out.println(correctKeyword + "/" + totalKeyword + " keywords are matching");
    return (double) correctKeyword * 100 / totalKeyword;
  }

  private double getSimilarityByMachineLearning(List<Instance> instances, List<Sentence> document) {
    return WekaService.getKeywordResult(instances, document).getRecall();
  }
}
