package keti.sgs.model;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class BlockChainInfo {
  
  long nonce;

  String chainIdHash;
  
  @Override
  public String toString() {
    return new Gson().toJsonTree(this).toString();
  }
  
}
