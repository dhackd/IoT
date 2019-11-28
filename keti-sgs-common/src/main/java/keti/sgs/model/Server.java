package keti.sgs.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "SERVERS")
@Data
public class Server {
  @Id
  @Column(name = "SERVER_ID", length = 15, nullable = false)
  protected String serverId;
  
  @Column(name = "NODE_ID", length = 15, nullable = false)
  protected String nodeId;

  @Column(name = "DOAMIN", length = 50, nullable = true)
  protected String domain;

  @Column(name = "CREATED_AT")
  protected Timestamp createAt;
}
