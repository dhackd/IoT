package keti.sgs.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import keti.sgs.model.Devices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceJpaRepository extends JpaRepository<Devices, String> {
  Optional<Devices> findByDeviceId(String identifier);

  Optional<Devices> findByDeviceAddr(String deviceAddr);

  // device search for monitor
  Page<Devices> findAllByDeviceTypeCodeAndCreateAtBetweenAndDeviceId(String deviceType,
      Date startDate, Date endDate, String deviceId, Pageable page);

  Page<Devices> findAllByDeviceTypeCodeAndCreateAtBetweenAndDeviceAddr(String deviceType,
      Date startDate, Date endDate, String deviceAddr, Pageable page);

  Page<Devices> findAllByDeviceTypeCodeAndCreateAtBetween(String deviceType, Date startDate,
      Date endDate, Pageable page);

  Page<Devices> findAllByDeviceTypeCode(String deviceType, Pageable page);

  List<Devices> findAllByDeviceTypeCode(String deviceType);

  Page<Devices> findAllByDeviceTypeCodeAndDeviceId(String deviceType, String deviceId,
      Pageable page);

  List<Devices> findAllByDeviceTypeCodeAndDeviceId(String deviceType, String deviceId);

  Page<Devices> findAllByDeviceTypeCodeAndDeviceAddr(String deviceType, String deviceAddr,
      Pageable page);

  List<Devices> findAllByDeviceTypeCodeAndDeviceAddr(String deviceType, String deviceAddr);

  Page<Devices> findAllByCreateAtBetweenAndDeviceAddr(Date startDate, Date endDate,
      String deviceAddr, Pageable page);


  Page<Devices> findAllByCreateAtBetweenAndDeviceId(Date startDate, Date endDate,
      String deviceId, Pageable page);

  Page<Devices> findAllByCreateAtBetween(Date startDate, Date endDate, Pageable page);

  Page<Devices> findAllByDeviceId(String deviceId, Pageable page);

  List<Devices> findAllByDeviceId(String deviceId);

  Page<Devices> findAllByDeviceAddr(String deviceAddr, Pageable page);

  List<Devices> findAllByDeviceAddr(String deviceAddr);
  
  Page<Devices> findAllByOrderByDataUpdatedAtDesc(Pageable page);
}
