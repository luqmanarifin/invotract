package jp.co.multibook.invotract.pdf2sentence;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by luqmanarifin on 6/29/17.
 */
public class PdfToSentence {

  public PdfToSentence() {

  }

  /**
   *
   * @param filePath PDF file path
   * @return path to sentence file
   */
  public static String getSentence(String filePath) {
    String xmlPath = getXmlOcr(filePath);
    List<Sentence> words = extract(xmlPath);
    List<Sentence> sentences = arrangeSentence(words);
    return writeToFile(filePath, sentences);
  }

  /**
   *
   * @return path to the result XML file
   */
  private static String getXmlOcr(String filePath) {
    Process process = null;
    try {
      process = new ProcessBuilder("tet", "--tetml", "wordplus", filePath).start();
      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String basePath = FilenameUtils.removeExtension(filePath);
    return basePath + ".tetml";
  }

  /**
   *
   * @param xmlPath the path to the xml
   * @return path file to the sentences of the document, using format
   * X Y size sentence
   * one line per sentence
   */
  private static List<Sentence> extract(String xmlPath) {
    return null;
  }

  /**
   * Arrange sentence from words
   * Sentence example: ABC DEF Company
   * Word example: ABC, DEF, Company
   * We should arrange a sentence from consecutive word, using Depth-First Search
   *
   * @param words
   * @return
   */
  private static List<Sentence> arrangeSentence(List<Sentence> words) {
    return null;
  }

  /**
   *
   * @param pdfName
   * @param sentences the sentence we got from previous method
   * @return path file to the written sentence
   */
  private static String writeToFile(String pdfName, List<Sentence> sentences) {
    return null;
  }

}
