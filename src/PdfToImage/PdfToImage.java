package PdfToImage;

import org.apache.commons.io.FilenameUtils;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class PdfToImage {

  public PdfToImage() {

  }

  /**
   *
   * @param pdfPath
   * @return path to the images of PDF. different page of PDF means different imagefile
   */
  public static List<String> getImage(String pdfPath) {
    System.out.println("enter the function!");
    List<String> outputFiles = new ArrayList<>();

    try {

      // get the filename without extension
      String basename = FilenameUtils.removeExtension(pdfPath);
      System.out.println(pdfPath);
      System.out.println(basename);

      // load PDF document
      PDFDocument document = new PDFDocument();
      document.load(new File(pdfPath));

      // create renderer
      SimpleRenderer renderer = new SimpleRenderer();

      // set resolution (in DPI)
      renderer.setResolution(300);

      // render
      List<Image> images = renderer.render(document);

      // write images to files to disk as JPG
      try {
        for (int i = 0; i < images.size(); i++) {
          String filename = basename + (i + 1) + ".png";
          ImageIO.write((RenderedImage) images.get(i), "png", new File(filename));
          outputFiles.add(filename);
        }
      } catch (IOException e) {
        System.out.println("ERROR: " + e.getMessage());
      }

    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
    }
    return outputFiles;
  }

}
