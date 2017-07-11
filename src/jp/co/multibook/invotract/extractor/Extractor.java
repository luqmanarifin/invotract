package jp.co.multibook.invotract.extractor;

import jp.co.multibook.invotract.common.Common;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.javatuples.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Using Stanford NER binary distribution, add it to $PATH to ner.sh can be accessed everywhere
 *
 * Created by luqmanarifin on 6/26/17.
 */
public class Extractor {

  private List<String> lines;
  private String text;
  private String filePath;

  private static char[] dateDelimiters = {'-', '/'};
  private static char[] amountProhibited = {'-', '/', '#', '%', '—'};
  private static String[] rowProhibited = {"from", "bill", "to", "invoice"};

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
    if (date != null) {
      json.put("date", date.serialize());
    }
    json.put("taxRate", taxRate);
    JSONArray jsonItems = new JSONArray();
    for (Item item : items) {
      jsonItems.add(item.serialize());
    }
    json.put("items", jsonItems);
    return json.toJSONString();
  }

  private String getCompanyName() {
    Process process = null;
    try {
      process = new ProcessBuilder("ner.sh", filePath).start();
      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    InputStream inputStream = process.getInputStream();
    String nerResult = null;
    try {
      nerResult = IOUtils.toString(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Common.writeFile(filePath + ".ner", nerResult);

    List<String> possibleCompanyName = new ArrayList<>();
    Scanner scanner = new Scanner(nerResult);
    boolean wordBeforeIsCompany = false;
    while (scanner.hasNext()) {
      String word = scanner.next();
      String[] tokens = word.split("/");
      if (tokens[tokens.length - 1].equals("ORGANIZATION")) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < tokens.length; i++) {
          if (i > 0) {
            sb.append("/");
          }
          sb.append(tokens[i]);
        }
        String name = sb.toString();

        StringBuilder stringBuilder = new StringBuilder();
        if (wordBeforeIsCompany) {
          stringBuilder.append(possibleCompanyName.get(possibleCompanyName.size() - 1));
          stringBuilder.append(" ");
          possibleCompanyName.remove(possibleCompanyName.size() - 1);
        }
        stringBuilder.append(name);
        possibleCompanyName.add(stringBuilder.toString());
        wordBeforeIsCompany = true;
      } else {
        wordBeforeIsCompany = false;
      }
    }
    System.out.println("possible company name:");
    for (String name : possibleCompanyName) {
      System.out.println("- " + name);
    }
    System.out.println();
    return possibleCompanyName.isEmpty()? "" : possibleCompanyName.get(0);
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

  private static boolean isPureNumber(String word) {
    for (int i = 0; i < word.length(); i++) {
      if (!Character.isDigit(word.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private static long convertToAmount(String word) {
    word = word.split("\\.")[0];
    long number = 0;
    for (int i = 0; i < word.length(); i++) {
      if (Character.isDigit(word.charAt(i))) {
        number = number * 10 + (word.charAt(i) - '0');
      }
    }
    return number;
  }

  private static boolean isProbableAmount(String word) {
    for (int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      if (Character.isDigit(c)) continue;
      if (Character.isAlphabetic(c)) return false;
      for (int j = 0; j < amountProhibited.length; j++) {
        if (c == amountProhibited[j]) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean isProbableRow(String line) {
    String[] tokens = line.split("\\s");
    for (int i = 0; i < tokens.length; i++) {
      for (int j = 0; j < rowProhibited.length; j++) {
        if (tokens[i].equalsIgnoreCase(rowProhibited[j])) {
          return false;
        }
      }
    }
    int counterNotAmount = 0;
    for (int i = 0; i < tokens.length; i++) {
      if (!isProbableAmount(tokens[i])) {
        counterNotAmount++;
      }
    }
    return !StringUtils.containsIgnoreCase(line, "total")
      && !StringUtils.containsIgnoreCase(line, "tax")
      && tokens.length > 0
      && isProbableAmount(tokens[tokens.length - 1])
      && counterNotAmount > 0;
  }

  private static Pair<Integer, Integer> getLongestFalseRange(boolean[] a) {
    int beg = -1, end = -1, best = 0;
    int last = -1;
    for (int i = 0; i < a.length; i++) {
      if (a[i]) {
        last = i;
      }
      if (i - last > best) {
        best = i - last;
        beg = last + 1;
        end = i;
      }
    }
    return new Pair<>(beg, end);
  }

  public static Item getItem(String line) {
    String[] tokens = line.split("\\s");
    boolean[] isAmount = new boolean[tokens.length];

    // looking for amount
    long biggestAmount = 0;
    for (int i = 0; i < tokens.length; i++) {
      if (isAmount[i] = isProbableAmount(tokens[i])) {
        biggestAmount = Math.max(biggestAmount, convertToAmount(tokens[i]));
      }
    }

    // looking for name
    Pair<Integer, Integer> nameRange = getLongestFalseRange(isAmount);
    StringBuilder sbName = new StringBuilder();
    for (int i = nameRange.getValue0(); i <= nameRange.getValue1(); i++) {
      if (i > nameRange.getValue0()) {
        sbName.append(" ");
      }
      sbName.append(tokens[i]);
    }

    // looking for quantity
    long quantity = -1;
    for (int i = 0; i + 1 < tokens.length; i++) {
      if (isPureNumber(tokens[i])) {
        quantity = Long.parseLong(tokens[i]);
        break;
      }
    }
    String name = sbName.toString();
    return new Item(name, biggestAmount);
  }

  private List<Item> getItems() {
    List<Item> items = new ArrayList<>();
    for (String line : lines) {
      if (isProbableRow(line)) {
        items.add(getItem(line));
      }
    }
    long tot = 0;
    for (int i = 0; i + 1 < items.size(); i++) {
      tot += items.get(i).getAmount();
    }

    // possibility of including TOTAL ROW
    if (!items.isEmpty() && tot == items.get(items.size() - 1).getAmount()) {
      items.remove(items.get(items.size() - 1));
    }
    return items;
  }

  private String getText() {
    return this.text;
  }

  private String cleanAfterPercent(String word) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < word.length(); i++) {
      sb.append(word.charAt(i));
      if (word.charAt(i) == '%') {
        break;
      }
    }
    return sb.toString();
  }

  private boolean isProbableTax(String word) {
    word = cleanAfterPercent(word);

    boolean alphabetExist = false;
    for (int i = 0; i < word.length(); i++) {
      if (Character.isAlphabetic(word.charAt(i))) {
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
    return cleanAfterPercent(word);
  }

  private boolean isProbableDate(String word) {
    for (char delimiter : dateDelimiters) {
      int[] count = new int[10];
      Arrays.fill(count, 0);
      String[] tokens = word.split(Character.toString(delimiter));
      boolean possible = tokens.length == 3;
      for (String token : tokens) {
        if (token.length() >= 10) {
          possible = false;
          break;
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
    for (char delimiter : dateDelimiters) {
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
