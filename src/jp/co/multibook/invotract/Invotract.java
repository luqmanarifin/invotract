package jp.co.multibook.invotract;

import jp.co.multibook.invotract.common.Common;
import jp.co.multibook.invotract.extractor.Extractor;
import jp.co.multibook.invotract.image2text.ImageToText;
import jp.co.multibook.invotract.pattern.PatternDistinguisher;
import jp.co.multibook.invotract.pdf2image.PdfToImage;
import jp.co.multibook.invotract.pdf2sentence.PdfToSentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Invotract {

  public static void main(String[] args) {
    /*
    if (args.length == 0) {
      System.out.println("Usage: java -jar invotract.jar <PDF file>");
      return;
    }
    */
    PatternDistinguisher patternDistinguisher = new PatternDistinguisher();
  }

  public static void pdfToJson(String[] args) {
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

  public static void pdfToSentence(String[] args) {
    String filename = args[0];
    String outputFile = PdfToSentence.getSentence(filename);
    System.out.println("Sentence result has been written to " + outputFile);
  }

}