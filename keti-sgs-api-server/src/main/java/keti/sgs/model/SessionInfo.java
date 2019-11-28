package keti.sgs.model;

import java.io.Serializable;
import javax.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;


@Data
@RedisHash("session")
public class SessionInfo implements Serializable {

  /**
   * .
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  public String id;

  private String addr;

  private String session;

  private String createTime;
}
