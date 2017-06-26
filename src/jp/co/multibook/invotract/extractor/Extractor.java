package jp.co.multibook.invotract.extractor;

import jp.co.multibook.invotract.common.Common;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Extractor {

  private List<String> lines;
  private String text;
  private String filePath;

  public Extractor(String filePath) {
    this.filePath = filePath;
    try {
      text = Common.readFile(filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scanner scanner = new Scanner(text);
    lines = new ArrayList<>();
    while (scanner.hasNextLine()) {
     lines.add(scanner.nextLine());
    }
    scanner.close();
  }

  public String getJsonSummary() {
    String companyName = getCompanyName();
    Date date = getDate();
    String taxRate = getTaxRate();
    List<Item> items = getItems();

    JSONObject json = new JSONObject();
    json.put("companyName", companyName);
    json.put("date", date.serialize());
    json.put("taxRate", getTaxRate());
    JSONArray jsonItems = new JSONArray();
    for (Item item : items) {
      jsonItems.add(item.serialize());
    }
    json.put("items", jsonItems);
    return json.toString();
  }

  private String getCompanyName() {
    return null;
  }

  private Date getDate() {
    return null;
  }

  private String getTaxRate() {
    for (String line : lines) {
      String[] tokens = line.split("\\s");
      for (String token : tokens) {
        if (isProbableTax(token)) {
          return convertToTax(token);
        }
      }
    }
    return "0";
  }

  private List<Item> getItems() {
    return null;
  }

  private String getText() {
    return this.text;
  }

  private boolean isProbableTax(String word) {
    word = word.trim();
    boolean alphabetExist = false;
    for (int i = 0; i < word.length(); i++) {
      if (Character.isDigit(word.charAt(i))) {
        alphabetExist = true;
      }
    }
    return !alphabetExist && word.charAt(word.length() - 1) == '%';
  }

  /**
   * Assumed that word is a valid tax rate
   * @param word
   * @return
   */
  private String convertToTax(String word) {
    String taxRate = "";
    for (int i = 0; i < word.length(); i++) {
      if (Character.isDigit(word.charAt(i))) {
        if (taxRate.length() == 1) {
          taxRate += ".";
        }
        taxRate += word.charAt(i);
      }
    }
    return taxRate;
  }

  private boolean isProbableDate(String word) {
    return false;
  }

}
