package jp.co.multibook.invotract.pattern.service;

import jp.co.multibook.invotract.common.Common;
import jp.co.multibook.invotract.pattern.model.Instance;
import jp.co.multibook.invotract.pattern.model.Result;
import jp.co.multibook.invotract.pdf2sentence.Sentence;
import weka.classifiers.trees.RandomTree;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by luqmanarifin on 7/6/17.
 */
public class WekaService {

  public WekaService() {
  }

  public static Result getKeywordResult(List<Instance> instances, List<Sentence> document) {
    String trainingText = PatternService.convertInstanceListToText(instances);
    List<Instance> documentInstances = new ArrayList<>();
    for (Sentence sentence : document) {
      documentInstances.add(sentence.toKeywordInstance());
    }
    String testText = PatternService.convertInstanceListToText(documentInstances);
    Common.writeFile("training.arff", trainingText);
    Common.writeFile("test.arff", testText);
    return learningTheModel("training.arff", "test.arff");
  }

  public static Result getResult(List<Instance> trainingInstances, List<Instance> testInstances) {
    String trainingText = PatternService.convertInstanceListToText(trainingInstances);
    String testText = PatternService.convertInstanceListToText(testInstances);
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
  private static Result learningTheModel(String trainingPath, String testPath) {
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

  private static String getWekaOutput(String[] args) {
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
