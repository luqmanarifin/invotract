package jp.co.multibook.invotract.extractor;

import jp.co.multibook.invotract.common.Serializable;
import org.json.simple.JSONObject;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Item implements Serializable {

  private String name;
  private long amount;

  public Item(String name, long amount) {
    this.name = name;
    this.amount = amount;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  @Override
  public JSONObject serialize() {
    JSONObject json = new JSONObject();
    json.put("name", name);
    json.put("amount", amount);
    return json;
  }

  @Override
  public void unserialize(JSONObject json) {
    this.name = (String) json.get("name");
    this.amount = (long) json.get("amount");
  }
}
