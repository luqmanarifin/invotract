package jp.co.multibook.invotract.pattern.model;

import jp.co.multibook.invotract.common.Common;

import java.util.Comparator;

/**
 * Created by luqmanarifin on 7/4/17.
 */
public class Instance implements Comparable<Instance> {

  private double x;
  private double y;
  private double size;
  private int clazz;

  public Instance() {

  }


  public Instance(double x, double y, double size, boolean clazz) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.clazz = (clazz? 1 : 0);
  }

  public Instance(double x, double y, double size, int clazz) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.clazz = clazz;
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

  public int getClazz() {
    return clazz;
  }

  public void setClazz(int clazz) {
    this.clazz = clazz;
  }

  public boolean isClazz() {
    return clazz == 1;
  }

  public void inject(String line) {
    String[] tokens = line.split(",");
    this.x = Double.parseDouble(tokens[0]);
    this.y = Double.parseDouble(tokens[1]);
    this.size = Double.parseDouble(tokens[2]);
    this.clazz = tokens[3].equals("yes")? 1 : 0;
  }

  @Override
  public String toString() {
    return x + "," + y + "," + size + "," + (clazz == 1? "yes" : (clazz == 0? "no" : "?"));
  }

  public int compareTo(Instance compareInstance) {
    return 0;
  }

  public static Comparator<Instance> compareByY = new Comparator<Instance>() {

    public int compare(Instance instance1, Instance instance2) {

      Double value1 = instance1.getY();
      Double value2 = instance2.getY();

      if (Math.abs(value1 - value2) < Common.PIXEL_TOLERANCE) {
        Double x1 = instance1.getX();
        Double x2 = instance2.getX();

        return x1.compareTo(x2);
      }

      //ascending order
      return value1.compareTo(value2);
    }

  };
}
