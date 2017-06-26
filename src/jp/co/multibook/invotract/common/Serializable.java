package jp.co.multibook.invotract.common;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public interface Serializable {

  String serialize();

  void unserialize(String json);

}

