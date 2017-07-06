package jp.co.multibook.invotract.pattern.model;

import java.util.List;

/**
 * Created by luqmanarifin on 7/6/17.
 */
public class CorrectData {

  private String date;
  private String company;
  private String tax;
  private List<String> rows;

  public CorrectData(String date, String company, String tax, List<String> rows) {
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

  public List<String> getRows() {
    return rows;
  }

  public void setRows(List<String> rows) {
    this.rows = rows;
  }
}
