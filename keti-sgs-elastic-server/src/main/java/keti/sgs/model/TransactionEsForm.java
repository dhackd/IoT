package keti.sgs.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "chain_*_tx", type = "tx")
public class TransactionEsForm {
  @Id
  private String txId;
  private String ts;
  private String blockno;
  private String from;
  private String to;
  private String amount;
  private String type;
  private String payload;
}
