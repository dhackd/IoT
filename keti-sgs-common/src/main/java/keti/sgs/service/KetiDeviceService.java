package keti.sgs.service;

import java.util.Optional;
import keti.sgs.model.Devices;
import keti.sgs.repository.DeviceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KetiDeviceService {

  @Autowired
  DeviceJpaRepository deviceJpaRepository;

  public Optional<Devices> getDeviceId(String identifier) {
    return deviceJpaRepository.findByDeviceId(identifier);
  }

  public Optional<Devices> getDeviceAddr(String address) {
    return deviceJpaRepository.findByDeviceAddr(address);
  }
}