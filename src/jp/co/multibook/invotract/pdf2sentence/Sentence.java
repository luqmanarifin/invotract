package jp.co.multibook.invotract.pdf2sentence;

import jp.co.multibook.invotract.common.Common;
import jp.co.multibook.invotract.common.Serializable;
import jp.co.multibook.invotract.pattern.PatternDistinguisher;
import jp.co.multibook.invotract.pattern.model.Instance;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by luqmanarifin on 6/29/17.
 */
public class Sentence implements Serializable{

  // x-y location and size of the font
  private double x;
  private double y;
  private double endX;
  private double size;

  private String text;
  private boolean clazz;

  public Sentence() {
  }

  public Sentence(double x, double y, double endX, double size, String text) {
    this.x = x;
    this.y = y;
    this.endX = endX;
    this.size = size;
    this.text = text;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getEndX() {
    return endX;
  }

  public void setEndX(double endX) {
    this.endX = endX;
  }

  public double getSize() {
    return size;
  }

  public void setSize(double size) {
    this.size = size;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public JSONObject serialize() {
    JSONObject json = new JSONObject();
    json.put("x", x);
    json.put("y", y);
    json.put("endX", endX);
    json.put("size", size);
    json.put("text", text);
    return json;
  }

  @Override
  public void unserialize(JSONObject json) {
    this.x = (double) json.get("x");
    this.y = (double) json.get("y");
    this.endX = (double) json.get("endX");
    this.size = (double) json.get("size");
    this.text = (String) json.get("text");
  }

  @Override
  public String toString() {
    String string = Double.toString(x) + ","
      + Double.toString(y) + ","
      + Double.toString(endX) + ","
      + Double.toString(size) + ","
      + "\"" + text.replaceAll("\"", "") + "\"";
    return string;
  }

  public Instance toKeywordInstance() {
    double x = this.getX();
    double y = this.getY();
    double size = this.getSize();
    boolean clazz = false;
    for (int i = 0; i < PatternDistinguisher.keywords.length && !clazz; i++) {
      if (StringUtils.containsIgnoreCase(this.getText(), PatternDistinguisher.keywords[i])) {
        clazz = true;
      }
    }
    return new Instance(x, y, size, clazz);
  }

  public Instance toDateInstance(String date) {
    double x = this.getX();
    double y = this.getY();
    double size = this.getSize();
    boolean clazz = containsDate(this.getText(), date);
    return new Instance(x, y, size, clazz);
  }

  public Instance toCompanyInstance(String company) {
    double x = this.getX();
    double y = this.getY();
    double size = this.getSize();
    boolean clazz = containsCompany(this.getText(), company);
    return new Instance(x, y, size, clazz);
  }

  public Instance toTaxInstance(String tax) {
    double x = this.getX();
    double y = this.getY();
    double size = this.getSize();
    boolean clazz = containsTax(this.getText(), tax);
    return new Instance(x, y, size, clazz);
  }

  public Instance toCorrectInstance() {
    double x = this.getX();
    double y = this.getY();
    double size = this.getSize();
    boolean clazz = true;
    return new Instance(x, y, size, clazz);
  }

  /**
   *
   * @param instances list of instance that have keyword at dictionaries, should be Y-sorted
   * @return
   */
  public Instance toRowInstance(List<Instance> instances) {
    double x = this.getX();
    double y = this.getY();
    double size = this.getSize();
    boolean clazz = inSameRow(instances);
    return new Instance(x, y, size, clazz);
  }

  private boolean containsDate(String text, String correctDate) {
    return text.equals(correctDate);
  }

  private boolean containsCompany(String text, String correctCompany) {
    return text.equals(correctCompany);
  }

  private boolean containsTax(String text, String correctTax) {
    return text.equals(correctTax);
  }

  /**
   *
   * @param instances sorted by Y coordinate
   * @return
   */
  private boolean inSameRow(List<Instance> instances) {
    int l = 0, r = instances.size() - 1;
    while (l < r) {
      int mid = (l + r) / 2;
      if (instances.get(mid).getY() < this.y) {
        l = mid + 1;
      } else {
        r = mid;
      }
    }
    return 0 <= l && l < instances.size()
      && Math.abs(instances.get(l).getY() - this.y) < Common.PIXEL_TOLERANCE;
  }


}
