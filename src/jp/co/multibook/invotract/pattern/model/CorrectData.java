package jp.co.multibook.invotract.pattern.model;

import jp.co.multibook.invotract.common.Serializable;
import jp.co.multibook.invotract.extractor.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by luqmanarifin on 7/6/17.
 */
public class CorrectData implements Serializable {

  private String date;
  private String company;
  private String tax;
  private List<Item> rows;

  public CorrectData(String date, String company, String tax, List<Item> rows) {
    this.date = date;
    this.company = company;
    this.tax = tax;
    this.rows = rows;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getTax() {
    return tax;
  }

  public void setTax(String tax) {
    this.tax = tax;
  }

  public List<Item> getRows() {
    return rows;
  }

  public void setRows(List<Item> rows) {
    this.rows = rows;
  }

  @Override
  public JSONObject serialize() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("date", date);
    jsonObject.put("company", company);
    jsonObject.put("tax", tax);
    JSONArray rowsJsonArray = new JSONArray();
    for (Item item : rows) {
      rowsJsonArray.add(item.serialize());
    }
    jsonObject.put("rows", rowsJsonArray);
    return jsonObject;
  }

  @Override
  public void unserialize(JSONObject json) {

  }
}
