package jp.co.multibook.invotract.extractor;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public class Item {

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
}
