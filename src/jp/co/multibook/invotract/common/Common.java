package jp.co.multibook.invotract.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Common {

  public Common() {

  }

  public static String readFile(String path) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded);
  }

  public static void writeFile(String path, String text) {
    PrintWriter out = null;
    try {
      out = new PrintWriter(path);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    out.println(text);
    out.close();
  }

  public static double PIXEL_TOLERANCE = 2;

}