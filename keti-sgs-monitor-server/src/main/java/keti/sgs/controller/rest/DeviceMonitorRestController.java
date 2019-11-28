package keti.sgs.controller.rest;

import static keti.sgs.util.Utils.pageRequest2Pagable;

import java.text.ParseException;
import keti.sgs.controller.AbstractRestController;
import keti.sgs.except.DuplicateAddressException;
import keti.sgs.except.DuplicateIdException;
import keti.sgs.model.Devices;
import keti.sgs.model.ResultMsg;
import keti.sgs.repository.DeviceJpaRepository;
import keti.sgs.service.DeviceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceMonitorRestController extends AbstractRestController {

  @Autowired
  DeviceJpaRepository deviceJpaRepository;

  @Autowired
  DeviceMonitorService deviceService;

  /**
   * get device list by query.
   * @param type type
   * @param startDate start date
   * @param endDate end date
   * @param searchType search type
   * @param searchData search data
   * @param pageNum page number
   */
  @GetMapping("devices/{pageNum}")
  public ResultMsg getDeviceList(String type, String startDate, String endDate, String searchType,
      String searchData, @PathVariable int pageNum) throws ParseException {
    logger.info(
        "[DC] query device >>>>> deviceType: {}, startDate: {}, endDate: {},"
            + " searchType: {}, searchData: {}, page: {} ",
        type, startDate, endDate, searchType, searchData, pageNum);

    Page<Devices> result = deviceService.getDeviceList(type, startDate, endDate, searchType,
        searchData, pageRequest2Pagable(pageNum));

    ResultMsg resultMsg = new ResultMsg();
    resultMsg.setData(result);
    return resultMsg;
  }

  /**
   * register device.
   * @param deviceId device id
   * @param typeCode device type code
   * @param deviceAddr device address
   */
  @PostMapping("devices")
  public ResultMsg registerDevice(String deviceId, String typeCode, String deviceAddr) {
    logger.info("[DC] register device: {} ", deviceId);

    if (deviceService.isDuplicatedId(deviceId)) {
      throw new DuplicateIdException();
    }

    if (deviceService.isDuplicatedAddr(deviceAddr)) {
      throw new DuplicateAddressException();
    }
    
    String txHash = deviceService.registerDevice(deviceId, typeCode, deviceAddr);
    return new ResultMsg(txHash);
  }

  /**
   * remove device.
   * @param deviceId device id
   */
  @DeleteMapping("devices/{deviceId}")
  public ResultMsg removeDevice(@PathVariable String deviceId) {
    logger.info("[DC] delete device: {} ", deviceId);

    String txHash = deviceService.removeDevice(deviceId);

    return new ResultMsg(txHash);
  }
}
