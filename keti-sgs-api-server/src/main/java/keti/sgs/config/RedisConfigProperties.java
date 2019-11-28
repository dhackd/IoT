package keti.sgs.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "spring.redis.cluster")
public class RedisConfigProperties {
  /*
   * spring.redis.cluster.nodes[0] = 127.0.0.1:7379 spring.redis.cluster.nodes[1] = 127.0.0.1:7380
   * ...
   */
  List<String> nodes;
  /*
   * spring.redis.cluster.status = true:false
   * 
   */
  boolean status;

  /**
   * Get initial collection of known cluster nodes in format {@code host:port}.
   *
   * @return node list
   */
  public List<String> getNodes() {
    return nodes;
  }

  /**
   * Get initial boolean of know cluster nodes configuration value in format
   * {@code status:true or false}.
   * 
   * @return an true if the cluster node status value is true, otherwise an false
   */
  public boolean getStatus() {
    return status;
  }

  public void setNodes(List<String> nodes) {
    this.nodes = nodes;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }
}
