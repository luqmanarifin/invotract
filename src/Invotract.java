import ImageToText.ImageToText;
import PdfToImage.PdfToImage;

import java.util.List;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Invotract {

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: java Invotract [PDF/JPG/PNG FILE] [OUTPUT NAME]");
      return;
    }
    String filename = args[0];

    //String filename = "/home/luqmanarifin/Code/invotract/102-TAC.pdf";

    List<String> filenames = PdfToImage.getImage(filename);
    filename = ImageToText.getText(filenames);
  }

}
