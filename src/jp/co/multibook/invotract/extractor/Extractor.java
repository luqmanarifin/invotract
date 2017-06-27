package jp.co.multibook.invotract.extractor;

import jp.co.multibook.invotract.common.Common;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Extractor {

  private List<String> lines;
  private String text;
  private String filePath;

  private static char[] delimiters = {'-', '/'};

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
    json.put("taxRate", taxRate);
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
    for (String line : lines) {
      String[] tokens = line.split("\\s");
      for (String token : tokens) {
        if (isProbableDate(token)) {
          return convertToDate(token);
        }
      }
    }
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
    return !alphabetExist && word.endsWith("%");
  }

  /**
   *
   * @param word assumtion: word is a valid tax rate
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
    for (char delimiter : delimiters) {
      int[] count = new int[10];
      Arrays.fill(count, 0);
      String[] tokens = word.split(Character.toString(delimiter));
      boolean possible = tokens.length == 3;
      for (String token : tokens) {
        if (token.length() >= 10) {
          possible = false;
        }
        count[token.length()]++;
      }
      if (possible) {
        if (count[4] == 1 && count[2] == 2) return true;
        if (count[2] == 3) return true;
      }
    }
    return false;
  }

  private Date convertToDate(String word) {
    for (char delimiter : delimiters) {
      String[] tokens = word.split(Character.toString(delimiter));
      boolean possible = tokens.length == 3;
      for (String token : tokens) {
        if (token.length() >= 10) {
          possible = false;
        }
      }
      if (possible) {
        Date date = null;
        if (tokens[0].length() == 4) {
          date = new Date(Integer.parseInt(tokens[2]),
                          Integer.parseInt(tokens[1]),
                          Integer.parseInt(tokens[0]));
        } else if (tokens[2].length() == 4) {
          date = new Date(Integer.parseInt(tokens[0]),
            Integer.parseInt(tokens[1]),
            Integer.parseInt(tokens[2]));
        } else {                      // default format: DD/MM/YY
          date = new Date(Integer.parseInt(tokens[0]),
            Integer.parseInt(tokens[1]),
            Integer.parseInt(tokens[2]));
        }
        return date;
      }
    }
    return null;
  }

}
