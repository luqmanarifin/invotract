package jp.co.multibook.invotract.common;

import org.json.simple.JSONObject;

/**
 * Created by luqmanarifin on 6/26/17.
 */
public interface Serializable {

  JSONObject serialize();

  void unserialize(JSONObject json);

}

