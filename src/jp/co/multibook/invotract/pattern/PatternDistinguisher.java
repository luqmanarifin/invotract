package jp.co.multibook.invotract.pattern;

import jp.co.multibook.invotract.common.Common;
import jp.co.multibook.invotract.pattern.model.Instance;
import jp.co.multibook.invotract.pattern.model.Pattern;
import jp.co.multibook.invotract.pattern.model.Result;
import jp.co.multibook.invotract.pattern.service.PatternService;
import jp.co.multibook.invotract.pdf2sentence.PdfToSentence;
import jp.co.multibook.invotract.pdf2sentence.Sentence;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import weka.classifiers.trees.RandomTree;

import java.io.*;
import java.util.*;

/**
 * Created by luqmanarifin on 7/3/17.
 */
public class PatternDistinguisher {

  private final static double similarPercentage = 80;
  private final static double fontSizeError = 0.2;
  private final static int keywordLimit = 30;
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
    System.out.println("testing DB connection");
    List<Pattern> patterns = PatternService.getDatePatterns();
    if (!patterns.isEmpty()) {
      System.out.println("DB connected");
    }
  }

  public void addPattern(String pdfFile) {
    List<Sentence> sentences = PdfToSentence.getSentenceList(pdfFile);
    Pattern sourcePattern = getSimilarPattern(sentences);
    List<Instance> instances = new ArrayList<>();
    for (Sentence sentence : sentences) {
      instances.add(sentence.toInstance());
    }
    PatternService.addPattern("keyword", sourcePattern, instances, pdfFile);
  }

  /**
   *
   * @param document
   * @return null, if this PDF file does not similar to any file
   *         pattern, the most similar PDF file with this file
   */
  public Pattern getSimilarPattern(List<Sentence> document) {
    List<Pattern> patterns = PatternService.getKeywordPatterns();
    double best = -1;
    int index = -1;
    for (int i = 0; i < patterns.size(); i++) {
      double similarityValue = similarity(patterns.get(i), document);
      if (similarityValue > best) {
        best = similarityValue;
        index = i;
      }
      System.out.println("similarity " + similarityValue + "% with index " + patterns.get(i).getId());
      System.out.printf("");
    }
    if (index == -1 || best < similarPercentage) {
      System.out.println("not found any similar pattern before");
      return null;
    } else {
      System.out.println("similar with pattern index: " + patterns.get(index).getId());
      System.out.println("pdf path: " + patterns.get(index).getFilePath());
      System.out.println("similarity percentage: " + best);
      System.out.println();
      return patterns.get(index);
    }
  }

  private double similarity(Pattern pattern, List<Sentence> document) {
    boolean[] isKeyword = new boolean[document.size()];
    for (int i = 0; i < document.size(); i++) {
      for (int j = 0; j < keywords.length; j++) {
        isKeyword[i] = StringUtils.containsIgnoreCase(document.get(i).getText(), keywords[j]);
        if (isKeyword[i]) {
          System.out.println(document.get(i).getText());
          break;
        }
      }
    }
    List<Instance> instances = PatternService.convertTextToInstanceList(pattern.getSentences());
    int yesClass = 0;
    for (Instance instance : instances) {
      if (instance.isClazz()) {
        yesClass++;
      }
    }
    if (yesClass < keywordLimit) {
      return getSimilarityByGeometry(instances, document, isKeyword);
    } else {
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
    System.out.println(correctKeyword + "/" + totalKeyword + " keywords are matching");
    return (double) correctKeyword * 100 / totalKeyword;
  }

  private double getSimilarityByMachineLearning(List<Instance> instances, List<Sentence> document) {
    return getResultMachineLearning(instances, document).getRecall();
  }

  private Result getResultMachineLearning(List<Instance> instances, List<Sentence> document) {
    String trainingText = PatternService.convertInstanceListToText(instances);
    List<Instance> documentInstances = new ArrayList<>();
    for (Sentence sentence : document) {
      documentInstances.add(sentence.toInstance());
    }
    String testText = PatternService.convertInstanceListToText(documentInstances);
    Common.writeFile("training.arff", trainingText);
    Common.writeFile("test.arff", testText);
    return learningTheModel("training.arff", "test.arff");
  }

  /**
   *
   * @param trainingPath path to trainingPath .arff file
   * @param testPath path to testPath .arff file
   * @return boolean class of prediction result
   */
  private Result learningTheModel(String trainingPath, String testPath) {
    String[] recallArgs = {
      "-t", trainingPath,
      "-T", testPath,
      "-K", "0", "-M", "1.0", "-V", "0.001", "-S", "1"};
    String rawOutputRecall = getWekaOutput(recallArgs);
    Scanner scanner = new Scanner(rawOutputRecall);
    int n = -1;
    double recall = -1;
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = line.trim();
      String[] token = line.split("\\s+");
      if (token.length == 5) {
        if (token[0].equals("Total") && token[1].equals("Number")
          && token[2].equals("of") && token[3].equals("Instances")) {
          n = Integer.parseInt(token[4]);
        }
      }
      if (token.length == 9) {
        if (token[8].equals("yes")) {
          recall = Double.parseDouble(token[3]) * 100;
        }
      }
    }

    String[] instancesArgs = {
      "-t", trainingPath,
      "-T", testPath,
      "-K", "0", "-M", "1.0", "-V", "0.001", "-S", "1",
      "-classifications", "\"weka.classifiers.evaluation.output.prediction.CSV\""};
    String rawOutputInstances = getWekaOutput(instancesArgs);
    scanner = new Scanner(rawOutputInstances);
    boolean[] predictions = new boolean[n];
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] token = line.split(",");
      if (token.length == 5) {
        try {
          int id = Integer.parseInt(token[0]) - 1;
          predictions[id] = token[2].split(":")[1].equals("yes");
        } catch (Exception e) {

        }
      }
    }
    return new Result(recall, predictions);
  }

  private String getWekaOutput(String[] args) {
    // Create a stream to hold the output
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    // IMPORTANT: Save the old System.out!
    PrintStream old = System.out;
    // Tell Java to use your special stream
    System.setOut(ps);

    RandomTree.main(args);

    // Put things back
    System.out.flush();
    System.setOut(old);
    // Show what happened
    return baos.toString();
  }
}
