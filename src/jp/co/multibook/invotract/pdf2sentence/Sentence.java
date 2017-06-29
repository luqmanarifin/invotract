package jp.co.multibook.invotract.pdf2sentence;

import jp.co.multibook.invotract.common.Serializable;
import org.json.simple.JSONObject;

/**
 * Created by luqmanarifin on 6/29/17.
 */
public class Sentence implements Serializable{

  // x-y location and size of the font
  private double x;
  private double y;
  private double size;

  private String text;

  public Sentence(double x, double y, double size, String text) {
    this.x = x;
    this.y = y;
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
    json.put("size", size);
    json.put("text", text);
    return json;
  }

  @Override
  public void unserialize(JSONObject json) {
    this.x = (double) json.get("x");
    this.y = (double) json.get("y");
    this.size = (double) json.get("size");
    this.text = (String) json.get("text");
  }

  @Override
  public String toString() {
    String string = Double.toString(x) + ","
                  + Double.toString(y) + ","
                  + Double.toString(size) + ","
                  + "\"" + text.replaceAll("\"", "") + "\"";
    return string;
  }
}
