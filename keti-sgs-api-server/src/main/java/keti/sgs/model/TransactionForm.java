package keti.sgs.model;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class TransactionForm {

  int nonce;

  String from;

  String to;

  String chainIdHash;

  String amount;

  String payload;

  String sign;
  
  String hash;

  @Override
  public String toString() {
    return new Gson().toJson(this).toString();
  }
}
