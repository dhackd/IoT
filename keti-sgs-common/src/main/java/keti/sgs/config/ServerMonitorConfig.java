package keti.sgs.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import keti.sgs.model.Server;
import keti.sgs.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "monitor")
public class ServerMonitorConfig {
  @Autowired
  ServerRepository serverRepository;

  private List<HashMap<String, String>> servers = new ArrayList<>();

  public void setServers(List<HashMap<String, String>> servers) {
    this.servers = servers;
  }

  @PostConstruct
  void setUp() {
    this.servers.forEach(t -> {
      Server server = new Server();
      server.setServerId(t.get("name"));
      server.setNodeId(t.get("name"));
      server.setDomain(t.get("domain"));
      serverRepository.save(server);
    });
  }
}
