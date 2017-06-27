package jp.co.multibook.invotract.image2text;

import jp.co.multibook.invotract.common.Common;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class ImageToText {

  public ImageToText() {

  }

  /**
   * Better quality
   * @return the path of the resulting OCR text
   */
  public static String getText(List<String> filepaths) {
    String basename = FilenameUtils.removeExtension(filepaths.get(0));

    String allText = "";
    Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
    instance.setDatapath(System.getenv("TESSDATA_PREFIX"));
    for (String filepath : filepaths) {
      try {
        Process process = new ProcessBuilder("tesseract", filepath, filepath + ".ocr").start();
        try {
          process.waitFor();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        allText += Common.readFile(filepath + ".ocr.txt");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println(allText);
    String resultPath = basename + ".txt";
    Common.writeFile(resultPath, allText);
    return resultPath;
  }

  /**
   * Bad quality
   * @return the path of the resulting OCR text
   */
  public static String getTextUsingTess4J(List<String> filepaths) {
    System.out.println("wanna get text");
    String env = System.getenv("TESSDATA_PREFIX");
    System.out.println("environment " + env);

    String basename = FilenameUtils.removeExtension(filepaths.get(0));

    String allText = "";
    Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
    instance.setDatapath(System.getenv("TESSDATA_PREFIX"));
    for (String filepath : filepaths) {
      try {
        allText += instance.doOCR(new File(filepath));
      } catch (TesseractException e) {
        e.printStackTrace();
      }
    }
    System.out.println(allText);
    String resultPath = basename + ".txt";
    Common.writeFile(resultPath, allText);
    return resultPath;
  }

}
