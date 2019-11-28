package keti.sgs.service;

import java.util.List;
import keti.sgs.model.Server;
import keti.sgs.model.Type;
import keti.sgs.repository.ServerRepository;
import keti.sgs.repository.TypeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RenderService extends AbstractService {

  @Autowired
  TypeJpaRepository typeJpaRepository;
  
  @Autowired
  ServerRepository serverRepository;
  
  public List<Type> getTypeList() {
    return typeJpaRepository.findAll();
  }
  
  public List<Server> getServerList() {
    return serverRepository.findAll();
  }
}
