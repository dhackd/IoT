package keti.sgs.model;

import lombok.Data;

@Data
public class RenderTxForm {
  String deviceId;
  
  Type type;
  
  String from;
  
  String to;
  
  String txid;
  
  String regAt;
}
