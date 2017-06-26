package jp.co.multibook.invotract.common;

import java.io.IOException;
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

}