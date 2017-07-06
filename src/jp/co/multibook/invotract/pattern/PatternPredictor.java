package jp.co.multibook.invotract.pattern;

import jp.co.multibook.invotract.common.Common;
import jp.co.multibook.invotract.extractor.Extractor;
import jp.co.multibook.invotract.extractor.Item;
import jp.co.multibook.invotract.pattern.model.CorrectData;
import jp.co.multibook.invotract.pattern.model.Instance;
import jp.co.multibook.invotract.pattern.model.Pattern;
import jp.co.multibook.invotract.pattern.model.Result;
import jp.co.multibook.invotract.pattern.service.PatternService;
import jp.co.multibook.invotract.pattern.service.WekaService;
import jp.co.multibook.invotract.pdf2sentence.PdfToSentence;
import jp.co.multibook.invotract.pdf2sentence.Sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by luqmanarifin on 7/6/17.
 */
public class PatternPredictor {

  public PatternPredictor() {
  }

  public CorrectData getPrediction(String pdfFile) throws Exception {
    List<Sentence> sentences = PdfToSentence.getSentenceList(pdfFile);
    PatternDistinguisher patternDistinguisher = new PatternDistinguisher();
    Pattern sourceKeyword = patternDistinguisher.getSimilarPattern(sentences);
    Pattern sourceDate = (sourceKeyword == null? null : PatternService.getPattern("date", sourceKeyword.getId()));
    Pattern sourceCompany = (sourceKeyword == null? null : PatternService.getPattern("company", sourceKeyword.getId()));
    Pattern sourceTax = (sourceKeyword == null? null : PatternService.getPattern("tax", sourceKeyword.getId()));
    Pattern sourceRow = (sourceKeyword == null? null : PatternService.getPattern("row", sourceKeyword.getId()));

    String date = getStringPrediction(sourceDate, sentences);
    String company = getStringPrediction(sourceCompany, sentences);
    String tax = getStringPrediction(sourceTax, sentences);
    List<Item> rows = getRowPrediction(sourceRow, sentences);
    return new CorrectData(date, company, tax, rows);
  }

  private String getStringPrediction(Pattern pattern, List<Sentence> sentences) {
    if (pattern == null) return "";
    List<Instance> unknownInstances = toUnknownInstances(sentences);
    Result result = WekaService.getResult(pattern.getSentences(), unknownInstances);
    boolean[] prediction = result.getPrediction();
    for (int i = 0; i < prediction.length; i++) {
      if (prediction[i]) {
        return sentences.get(i).getText();
      }
    }
    return "";
  }

  private List<Item> getRowPrediction(Pattern pattern, List<Sentence> sentences) {
    if (pattern == null) return new ArrayList<>();
    Collections.sort(sentences, Sentence.compareByY);

    List<Instance> unknownInstances = toUnknownInstances(sentences);
    Result result = WekaService.getResult(pattern.getSentences(), unknownInstances);
    boolean[] prediction = result.getPrediction();
    for (int i = 0; i < prediction.length; i++) {
      for (int j = 0; j < prediction.length; j++) {
        if (prediction[i] && Math.abs(sentences.get(i).getY() - sentences.get(j).getY()) < Common.PIXEL_TOLERANCE) {
          prediction[j] = true;
        }
      }
    }
    List<Sentence> rowSentences = new ArrayList<>();
    for (int i = 0; i < prediction.length; i++) {
      if (prediction[i]) {
        rowSentences.add(sentences.get(i));
      }
    }
    List<Item> results = new ArrayList<>();
    for (int i = 0; i < rowSentences.size(); i++) {
      int j = i;
      List<Sentence> lineSentences = new ArrayList<>();
      while (j + 1 < rowSentences.size()
        && Math.abs(rowSentences.get(j).getY() - rowSentences.get(j + 1).getY())
        < Common.PIXEL_TOLERANCE) {
        j++;
        lineSentences.add(rowSentences.get(j));
      }
      i = j;
      Collections.sort(lineSentences, Sentence.compareByX);
      String line = "";
      for (Sentence sentence : lineSentences) {
        line += sentence.getText() + " ";
      }
      results.add(Extractor.getItem(line));
    }
    return results;
  }

  private List<Instance> toUnknownInstances(List<Sentence> sentences) {
    List<Instance> instances = new ArrayList<>();
    for (Sentence sentence : sentences) {
      instances.add(sentence.toUnknownInstance());
    }
    return instances;
  }

}
