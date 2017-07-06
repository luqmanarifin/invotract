package jp.co.multibook.invotract;

import jp.co.multibook.invotract.common.Common;
import jp.co.multibook.invotract.extractor.Extractor;
import jp.co.multibook.invotract.extractor.Item;
import jp.co.multibook.invotract.image2text.ImageToText;
import jp.co.multibook.invotract.pattern.PatternPoster;
import jp.co.multibook.invotract.pattern.PatternPredictor;
import jp.co.multibook.invotract.pattern.model.CorrectData;
import jp.co.multibook.invotract.pattern.model.Result;
import jp.co.multibook.invotract.pdf2image.PdfToImage;
import jp.co.multibook.invotract.pdf2sentence.PdfToSentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Invotract {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java -jar invotract.jar <predict/post> <PDF file>");
      return;
    }
    System.out.println(args.length);
    for (String arg : args) System.out.println(arg);
    if (args[0].equals("predict")) {
      predict(args);
    } else if (args[0].equals("post")) {
      postTrainingPattern(args);
    } else {
      System.out.println("command not found");
    }
  }

  private static void predict(String[] args) throws Exception {
    PatternPredictor patternPredictor = new PatternPredictor();
    String pdfPath = args[1];
    CorrectData result = patternPredictor.getPrediction(pdfPath);
    System.out.println("date: " + result.getDate());
    System.out.println("company: " + result.getCompany());
    System.out.println("tax: " + result.getTax());
    System.out.println("rows:");
    for (Item item : result.getRows()) {
      System.out.println("\t" + item.getAmount() + "\t" + item.getName());
    }
  }

  private static void postTrainingPattern(String[] args) throws Exception {
    PatternPoster patternPoster = new PatternPoster();
    // command - pdf file path - date - company - tax - rows
    String pdfPath = args[1];
    String date = args[2];
    String company = args[3];
    String tax = args[4];
    List<Item> rows = new ArrayList<>();
    for (int i = 5; i < args.length; i++) {
      rows.add(new Item(args[i], 0));
    }
    patternPoster.addPattern(pdfPath, new CorrectData(date, company, tax, rows));
  }

  private static void pdfToJson(String[] args) {
    String filename = args[0];
    String output = "stdout";
    if (args.length > 1) {
      output = args[1];
    }
    List<String> filenames = new ArrayList<>();
    if (filename.endsWith(".pdf")) {
      filenames = PdfToImage.getImage(filename);
    } else {
      filenames.add(filename);
    }
    filename = ImageToText.getText(filenames);
    Extractor extractor = new Extractor(filename);
    if (output.equals("stdout")) {
      System.out.println(extractor.getJsonSummary());
    } else {
      Common.writeFile(output, extractor.getJsonSummary());
    }
  }

  private static void pdfToSentence(String[] args) {
    String filename = args[0];
    String outputFile = PdfToSentence.getSentence(filename);
    System.out.println("Sentence result has been written to " + outputFile);
  }

}