package jp.co.multibook.invotract.extractor;

import jp.co.multibook.invotract.common.Serializable;

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
  public String serialize() {
    return null;
  }

  @Override
  public void unserialize(String json) {

  }
}
