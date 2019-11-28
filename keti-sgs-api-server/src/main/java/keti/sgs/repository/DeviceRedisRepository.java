package keti.sgs.repository;

import keti.sgs.model.SessionInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRedisRepository extends CrudRepository<SessionInfo, String> {

}
