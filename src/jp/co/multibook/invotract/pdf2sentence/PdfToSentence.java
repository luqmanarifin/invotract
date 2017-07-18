package jp.co.multibook.invotract.pdf2sentence;

import jp.co.multibook.invotract.common.Common;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    List<Sentence> sentences = getSentenceList(filePath);
    return writeToFile(filePath, sentences);
  }

  public static List<Sentence> getSentenceList(String filePath) {
    String xmlPath = getXmlOcr(filePath);
    List<Sentence> words = extract(xmlPath);
    return arrangeSentence(words);
  }

  /**
   *
   * @return path to the result XML file
   */
  private static String getXmlOcr(String filePath) {
    System.out.println("get xml ocr");

    String name = FilenameUtils.removeExtension(filePath) + ".tetml";

    Process process = null;
    try {
      process = new ProcessBuilder("tesseract", "--outfile", name, "--tetml", "wordplus", filePath).start();
      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return name;
  }

  /**
   *
   * @param xmlPath the path to the xml
   * @return path file to the sentences of the document, using format
   * X Y size sentence
   * one line per sentence
   */
  private static List<Sentence> extract(String xmlPath) {
    System.out.println("extract " + xmlPath);
    List<Sentence> results = new ArrayList<>();

    File file = new File(xmlPath);
    SAXReader saxReader = new SAXReader();
    Document document = null;
    try {
      document = saxReader.read(file);
    } catch (DocumentException e) {
      e.printStackTrace();
    }

    List<Element> pages = document.getRootElement().element("Document").element("Pages").elements("Page");
    System.out.println(pages.size() + " pages found!");
    for (Element page : pages) {
      page = page.element("Content");
      List<Element> paras = page.elements("Para");
      System.out.println(paras.size() + " direct para found!");
      for (Element para : paras) {
        List<Sentence> temp = getWordsFromPara(para);
        results.addAll(temp);
      }
      List<Element> tables = page.elements("Table");
      System.out.println(tables.size() + " table found!");
      for (Element table : tables) {
        List<Element> rows = table.elements("Row");
        for (Element row : rows) {
          List<Element> cells = row.elements("Cell");
          for (Element cell : cells) {
            List<Element> tableParas = cell.elements("Para");
            for (Element para : tableParas) {
              results.addAll(getWordsFromPara(para));
            }
          }
        }
      }
    }
    return results;
  }

  private static List<Sentence> getWordsFromPara(Element para) {
    List<Sentence> results = new ArrayList<>();
    List<Element> boxes = para.elements("Box");
    for (Element box : boxes) {
      List<Element> words = box.elements("Word");
      for (Element word : words) {
        results.add(getWordFromNode(word));
      }
    }
    List<Element> words = para.elements("Word");
    for (Element word : words) {
      results.add(getWordFromNode(word));
    }
    return results;
  }

  private static Sentence getWordFromNode(Element node) {
    double x = Double.parseDouble(node.element("Box").valueOf("@llx"));
    double y = Double.parseDouble(node.element("Box").valueOf("@lly"));
    double endX = Double.parseDouble(node.element("Box").valueOf("@urx"));
    double size = Double.parseDouble(node.element("Box").element("Glyph").valueOf("@size"));
    String text = node.element("Text").getText();
    return new Sentence(x, y, endX, size, text);
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
    int[] to = new int[words.size()];
    int[] received = new int[words.size()];
    int[] dist = new int[words.size()];
    Arrays.fill(to, -1);
    Arrays.fill(received, 0);

    for (int i = 0; i < words.size(); i++) {
      for (int j = 0; j < words.size(); j++) {
        if (i == j) continue;
        int cmp = distance(words.get(i), words.get(j));
        if (cmp != -1) {
          to[i] = j;
          received[j]++;
          dist[i] = cmp;
          break;
        }
      }
    }
    List<Sentence> sentences = new ArrayList<>();
    for (int i = 0; i < words.size(); i++) {
      if (received[i] > 0) continue;
      double x = words.get(i).getX();
      double y = words.get(i).getY();
      double endX = words.get(i).getEndX();
      double size = words.get(i).getSize();
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(words.get(i).getText());

      int now = i;
      while (to[now] != -1) {
        if (dist[now] == 1) {
          stringBuilder.append(" ");
        }
        stringBuilder.append(words.get(to[now]).getText());
        endX = Math.max(endX, words.get(to[now]).getEndX());
        now = to[now];
      }
      Sentence sentence = new Sentence(x, y, endX, size, stringBuilder.toString());
      sentences.add(sentence);
    }
    return sentences;
  }

  /**
   *
   * @param lef left word
   * @param rig right word
   * @return
   * 0 really near
   * 1 by space
   * -1 not a sentence (not related at all)
   */
  private static int distance(Sentence lef, Sentence rig) {
    // is not on one line
    if (Math.abs(lef.getY() - rig.getY()) > Common.PIXEL_TOLERANCE) return -1;

    // [lef] is not on the left of [rig]
    if (lef.getX() > rig.getX()) return -1;

    double averageWidth = (lef.getEndX() - lef.getX()) / lef.getText().length();
    double distance = rig.getX() - lef.getEndX();

    if (distance > 1.3 * averageWidth) return -1;       // too far away, not in a sentence
    if (distance < 0.3 * averageWidth) return 0;      // really near, in a word without space
    return 1;                                         // has space
  }

  /**
   *
   * @param pdfName
   * @param sentences the sentence we got from previous method
   * @return path file to the written sentence
   */
  private static String writeToFile(String pdfName, List<Sentence> sentences) {
    String all = "@RELATION invoices\n\n";
    all += "@ATTRIBUTE x REAL\n";
    all += "@ATTRIBUTE y REAL\n";
    all += "@ATTRIBUTE endX REAL\n";
    all += "@ATTRIBUTE size REAL\n";
    all += "@ATTRIBUTE text string\n";
    all += "@ATTRIBUTE class {yes, no}\n\n";

    all += "@DATA\n";
    for (Sentence sentence : sentences) {
      all += sentence.toString() + ",no\n";
    }
    String name = FilenameUtils.removeExtension(pdfName) + ".arff";
    Common.writeFile(name, all);
    return name;
  }

}
