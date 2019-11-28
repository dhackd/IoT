package keti.sgs.model;

import lombok.Data;

@Data
public class TransactionInfo {
  String deviceId;
  
  Type type;
  
  String from;

  String to;
  
  String txId;
  
  String blockHash;
  
  String payload;
}
