package jp.co.multibook.invotract.pattern;

import jp.co.multibook.invotract.pattern.model.CorrectData;
import jp.co.multibook.invotract.pattern.model.Instance;
import jp.co.multibook.invotract.pattern.model.Pattern;
import jp.co.multibook.invotract.pattern.service.PatternService;
import jp.co.multibook.invotract.pdf2sentence.PdfToSentence;
import jp.co.multibook.invotract.pdf2sentence.Sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by luqmanarifin on 7/6/17.
 */
public class PatternPoster {

  public PatternPoster() {
  }

  public void addPattern(String pdfFile, CorrectData correctData) throws Exception {
    List<Sentence> sentences = PdfToSentence.getSentenceList(pdfFile);
    PatternDistinguisher patternDistinguisher = new PatternDistinguisher();
    Pattern sourceKeyword = patternDistinguisher.getSimilarPattern(sentences);
    Pattern sourceDate = (sourceKeyword == null? null : PatternService.getPattern("date", sourceKeyword.getId()));
    Pattern sourceCompany = (sourceKeyword == null? null : PatternService.getPattern("company", sourceKeyword.getId()));
    Pattern sourceTax = (sourceKeyword == null? null : PatternService.getPattern("tax", sourceKeyword.getId()));
    Pattern sourceRow = (sourceKeyword == null? null : PatternService.getPattern("row", sourceKeyword.getId()));

    List<Instance> keywordInstances = toKeywordInstances(sentences);
    List<Instance> dateInstances = toDateInstances(sentences, correctData.getDate());
    List<Instance> companyInstances = toCompanyInstances(sentences, correctData.getCompany());
    List<Instance> taxInstances = toTaxInstances(sentences, correctData.getTax());
    List<Instance> rowInstances = toRowInstances(sentences, correctData.getRows());

    PatternService.addPattern("keyword", sourceKeyword, keywordInstances, pdfFile);
    PatternService.addPattern("date", sourceDate, dateInstances, pdfFile);
    PatternService.addPattern("company", sourceCompany, companyInstances, pdfFile);
    PatternService.addPattern("tax", sourceTax, taxInstances, pdfFile);
    PatternService.addPattern("row", sourceRow, rowInstances, pdfFile);
  }

  private List<Instance> toKeywordInstances(List<Sentence> sentences) {
    List<Instance> instances = new ArrayList<>();
    for (Sentence sentence : sentences) {
      instances.add(sentence.toKeywordInstance());
    }
    return instances;
  }

  private List<Instance> toDateInstances(List<Sentence> sentences, String correctDate) {
    List<Instance> instances = new ArrayList<>();
    for (Sentence sentence : sentences) {
      instances.add(sentence.toDateInstance(correctDate));
    }
    return instances;
  }

  private List<Instance> toCompanyInstances(List<Sentence> sentences, String correctCompany) {
    List<Instance> instances = new ArrayList<>();
    for (Sentence sentence : sentences) {
      instances.add(sentence.toCompanyInstance(correctCompany));
    }
    return instances;
  }

  private List<Instance> toTaxInstances(List<Sentence> sentences, String correctTax) {
    List<Instance> instances = new ArrayList<>();
    for (Sentence sentence : sentences) {
      instances.add(sentence.toTaxInstance(correctTax));
    }
    return instances;
  }

  private List<Instance> toRowInstances(List<Sentence> sentences, List<String> dictionaries) {
    List<Instance> correctInstances = new ArrayList<>();
    for (Sentence sentence : sentences) {
      if (dictionaries.contains(sentence.getText())) {
        correctInstances.add(sentence.toCorrectInstance());
      }
    }
    Collections.sort(correctInstances, Instance.compareByY);
    List<Instance> results = new ArrayList<>();
    for (Sentence sentence : sentences) {
      results.add(sentence.toRowInstance(correctInstances));
    }
    return results;
  }

}
