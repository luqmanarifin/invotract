package jp.co.multibook.invotract.extractor;

import jp.co.multibook.invotract.common.Common;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Extractor {

  private String text;

  public Extractor(String filePath) {
    try {
      text = Common.readFile(filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
    return null;
  }

  private List<Item> getItems() {
    return null;
  }

}
