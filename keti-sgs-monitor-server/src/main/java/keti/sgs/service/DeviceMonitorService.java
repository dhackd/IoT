package keti.sgs.service;

import static keti.sgs.util.Utils.string2endDate;
import static keti.sgs.util.Utils.string2startDate;

import java.text.ParseException;
import java.util.Date;
import keti.sgs.model.DeviceManagable;
import keti.sgs.model.Devices;
import keti.sgs.model.Type;
import keti.sgs.repository.DeviceJpaRepository;
import keti.sgs.repository.TypeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeviceMonitorService extends AbstractService implements DeviceManagable {
  @Autowired
  DeviceJpaRepository deviceJpaRepository;

  @Autowired
  TypeJpaRepository deviceTypeJpaRepository;

  @Autowired
  BlockChainMonitorService blockChainService;

  private enum Status {
    Register, Remove
  }

  
  @Override
  public String registerDevice(String deviceId, String deviceTypeCode, String deviceAddr) {
    Type deviceType = deviceTypeJpaRepository.findById(deviceTypeCode).get();
    Devices device = new Devices();
    device.setDeviceId(deviceId);
    device.setDeviceType(deviceType);
    device.setDeviceAddr(deviceAddr);

    String payload = getPayload(deviceId, Status.Register);
    String transactionId = blockChainService.insertBlockchain(payload, deviceAddr);
    
    device.setTransactionId(transactionId);
    deviceJpaRepository.save(device);

    return transactionId;
  }

  private String getPayload(String deviceId, Status register) {
    return new StringBuffer().append(deviceId).append(", ").append(register).toString();
  }

  @Override
  public String removeDevice(String deviceId) {
    Devices device = deviceJpaRepository.findById(deviceId).get();

    String payload = getPayload(deviceId, Status.Remove);
    String transactionId = blockChainService.insertBlockchain(payload, device.getDeviceAddr());

    deviceJpaRepository.deleteById(deviceId);
    
    return transactionId;
  }

  @Override
  public boolean isDuplicatedId(String deviceId) {
    return deviceJpaRepository.findById(deviceId).isPresent();
  }

  @Override
  public boolean isDuplicatedAddr(String deviceAddr) {
    return deviceJpaRepository.findByDeviceAddr(deviceAddr).isPresent();
  }

  /**
   * get device list by query.
   * 
   * @param deviceType device type
   * @param startDate start search date
   * @param endDate end search date
   * @param searchType search data type
   * @param searchData search data
   * @param page page number
   */
  public Page<Devices> getDeviceList(String deviceType, String startDate, String endDate,
      String searchType, String searchData, Pageable page) throws ParseException {
    Page<Devices> result = null;

    if (deviceType.equals("") || deviceType.equals("null")) {
      result = noType(startDate, endDate, searchType, searchData, page);
    } else {
      result = yesType(deviceType, startDate, endDate, searchType, searchData, page);
    }

    return result;
  }

  private Page<Devices> yesType(String deviceType, String startDate, String endDate,
      String searchType, String searchData, Pageable page) throws ParseException {
    Page<Devices> result = null;

    if (startDate.equals("") || endDate.equals("")) {
      result = yesTypeNoDate(deviceType, searchType, searchData, page);
    } else {
      result = yesTypeYesDate(deviceType, string2startDate(startDate), string2endDate(endDate),
          searchType, searchData, page);
    }

    return result;
  }

  private Page<Devices> noType(String startDate, String endDate, String searchType,
      String searchData, Pageable page) throws ParseException {
    Page<Devices> result = null;

    if (startDate.equals("") || endDate.equals("")) {
      result = noTypeNoDate(searchType, searchData, page);
    } else {
      result = noTypeYesDate(string2startDate(startDate), string2endDate(endDate), searchType,
          searchData, page);
    }

    return result;
  }

  private Page<Devices> yesTypeYesDate(String deviceType, Date startDate, Date endDate,
      String searchType, String searchData, Pageable page) {
    Page<Devices> result = null;

    if (searchData.equals("")) {
      result = yesTypeYesDateNoSearch(deviceType, startDate, endDate, page);
    } else {
      result =
          yesTypeYesDateYesSearch(deviceType, startDate, endDate, searchType, searchData, page);
    }

    return result;
  }

  private Page<Devices> yesTypeNoDate(String deviceType, String searchType, String searchData,
      Pageable page) {
    Page<Devices> result = null;

    if (searchData.equals("")) {
      result = yesTypeNoDateNoSearch(deviceType, page);
    } else {
      result = yesTypeNoDateYesSearch(deviceType, searchType, searchData, page);
    }

    return result;
  }

  private Page<Devices> noTypeYesDate(Date startDate, Date endDate, String searchType,
      String searchData, Pageable page) {
    Page<Devices> result = null;

    if (searchData.equals("")) {
      result = noTypeYesDateNoSearch(startDate, endDate, page);
    } else {
      result = noTypeYesDateYesSearch(startDate, endDate, searchType, searchData, page);
    }

    return result;
  }

  private Page<Devices> noTypeNoDate(String searchType, String searchData, Pageable page) {
    Page<Devices> result = null;

    if (searchData.equals("")) {
      result = noTypeNoDateNoSearch(page);
    } else {
      result = noTypeNoDateYesSearch(searchType, searchData, page);
    }

    return result;
  }

  private Page<Devices> yesTypeNoDateYesSearch(String deviceType, String searchType,
      String searchData, Pageable page) {
    logger.debug("[US] yesTypeNoDateYesSearch called");
    Page<Devices> result = null;

    if (searchType.equals("1")) {
      // 기기명 검색
      result = deviceJpaRepository.findAllByDeviceTypeCodeAndDeviceId(deviceType, searchData, page);
    } else if (searchType.equals("2")) {
      // 주소 검색
      result =
          deviceJpaRepository.findAllByDeviceTypeCodeAndDeviceAddr(deviceType, searchData, page);
    }
    return result;
  }

  private Page<Devices> yesTypeYesDateNoSearch(String deviceType, Date startDate, Date endDate,
      Pageable page) {
    logger.debug("[US] yesTypeYesDateNoSearch called");

    return deviceJpaRepository.findAllByDeviceTypeCodeAndCreateAtBetween(deviceType, startDate,
        endDate, page);

  }

  private Page<Devices> yesTypeNoDateNoSearch(String deviceType, Pageable page) {
    logger.debug("[US] yesTypeNoDateNoSearch called");

    return deviceJpaRepository.findAllByDeviceTypeCode(deviceType, page);
  }

  private Page<Devices> yesTypeYesDateYesSearch(String deviceType, Date startDate, Date endDate,
      String searchType, String searchData, Pageable page) {
    logger.debug("[US] yesTypeYesDateYesSearch called");
    Page<Devices> result = null;

    if (searchType.equals("1")) {
      // 기기명 검색
      result = deviceJpaRepository.findAllByDeviceTypeCodeAndCreateAtBetweenAndDeviceId(deviceType,
          startDate, endDate, searchData, page);
    } else if (searchType.equals("2")) {
      // 주소 검색
      result = deviceJpaRepository.findAllByDeviceTypeCodeAndCreateAtBetweenAndDeviceAddr(
          deviceType, startDate, endDate, searchData, page);
    }
    return result;

  }

  private Page<Devices> noTypeYesDateYesSearch(Date startDate, Date endDate, String searchType,
      String searchData, Pageable page) {
    logger.debug("[US] noTypeYesDateYesSearch called");
    Page<Devices> result = null;

    if (searchType.equals("1")) {
      // 기기명 검색
      result = deviceJpaRepository.findAllByCreateAtBetweenAndDeviceId(startDate, endDate,
          searchData, page);
    } else if (searchType.equals("2")) {
      // 주소 검색
      result = deviceJpaRepository.findAllByCreateAtBetweenAndDeviceAddr(startDate, endDate,
          searchData, page);
    }
    return result;

  }

  private Page<Devices> noTypeYesDateNoSearch(Date startDate, Date endDate, Pageable page) {
    logger.debug("[US] noTypeYesDateNoSearch called");
    return deviceJpaRepository.findAllByCreateAtBetween(startDate, endDate, page);
  }

  private Page<Devices> noTypeNoDateYesSearch(String searchType, String searchData, Pageable page) {
    logger.debug("[US] noTypeNoDateYesSearch called");
    Page<Devices> result = null;

    if (searchType.equals("1")) {
      // 기기명 검색
      result = deviceJpaRepository.findAllByDeviceId(searchData, page);
    } else if (searchType.equals("2")) {
      // 주소 검색
      result = deviceJpaRepository.findAllByDeviceAddr(searchData, page);
    }

    return result;
  }

  private Page<Devices> noTypeNoDateNoSearch(Pageable page) {
    logger.debug("[US] noTypeNoDateNoSearch called");
    return deviceJpaRepository.findAll(page);
  }
}
