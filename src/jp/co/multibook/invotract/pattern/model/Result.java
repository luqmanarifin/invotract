package jp.co.multibook.invotract.pattern.model;

/**
 * Created by luqmanarifin on 7/5/17.
 */
public class Result {

  private double recall;
  private boolean[] prediction;

  public Result(double recall, boolean[] prediction) {
    this.recall = recall;
    this.prediction = prediction;
  }

  public double getRecall() {
    return recall;
  }

  public void setRecall(double recall) {
    this.recall = recall;
  }

  public boolean[] getPrediction() {
    return prediction;
  }

  public void setPrediction(boolean[] prediction) {
    this.prediction = prediction;
  }
}
