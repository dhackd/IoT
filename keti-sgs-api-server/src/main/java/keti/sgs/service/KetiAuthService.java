package keti.sgs.service;

import static keti.sgs.util.Utils.pageRequest2Pagable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import keti.sgs.controller.AbstractController;
import keti.sgs.model.Devices;
import keti.sgs.model.RenderFormWithPage;
import keti.sgs.model.SessionInfo;
import keti.sgs.repository.DeviceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class KetiAuthService extends AbstractController {
  @Value("${spring.redis.session.expirate-time}")
  private int expiredtime;

  @Value("${spring.redis.session.session-pattern}")
  private String sessionPattern;

  @Autowired
  DeviceJpaRepository deviceJpaRepository;

  @Autowired
  RedisTemplate<String, Object> redis;

  @Autowired
  DeviceMonitorService deviceMonitorService;

  /**
   * createSession method.
   * 
   * @param addr device address
   * @param request http request
   * @return session info
   * @throws ParseException parse exception
   */
  public SessionInfo createSession(String addr, HttpServletRequest request) throws ParseException {
    logger.info("[KETI-AUTH-SERVER] createSession() >>>>>> addr:{}", addr);
    SessionInfo sessionInfo = new SessionInfo();
    HttpSession httpSession = request.getSession(true);
    sessionInfo.setAddr(addr);
    sessionInfo.setCreateTime(getDateTime());
    sessionInfo.setSession(httpSession.getId());

    ValueOperations<String, Object> vop = redis.opsForValue();
    vop.set(addr, sessionInfo);
    return sessionInfo;
  }

  /**
   * sessionCheck method.
   * 
   * @param addr device address
   * @param request http request
   * @return true or false
   * @throws ParseException parse exception
   */
  public boolean isSessionValidate(String addr, HttpServletRequest request) throws ParseException {
    SessionInfo sessionInfo = null;
    ValueOperations<String, Object> vop = redis.opsForValue();
    sessionInfo = (SessionInfo) vop.get(addr);
    logger.debug("[KETI-AUTH-SERVICE] isSessionValidate() >>>>>> sessionInfo:{}", sessionInfo);
    if (sessionInfo == null) {
      return false;
    }
    if (sessionInfo.getSession().equalsIgnoreCase(request.getHeader("KSESSIONID"))) {

      if (isSessionExpired(sessionInfo.getCreateTime())) {
        logger.debug("[KETI-AUTH-SERVICE] isSessionValidate() >>>>>> deleteExpiredSessionKey:{}",
            sessionInfo.getAddr());
        redis.delete(sessionInfo.getAddr());
        return false;
      }
      logger.debug("[KETI-AUTH-SERVICE] isSessionValidate() >>>>>> setCreateTime:{}",
          getDateTime());
      sessionInfo.setCreateTime(getDateTime());
      vop.set(addr, sessionInfo);
      return true;
    }
    return false;
  }

  /**
   * get device connection list(with redis).
   * 
   * @param page page number
   */
  public RenderFormWithPage<Devices> getDeviceConnectionStatusList(int page) throws ParseException {
    List<Devices> deviceList = new ArrayList<Devices>();

    // default search & 12 component per page
    Page<Devices> searchedDeviceList =
        deviceJpaRepository.findAllByOrderByDataUpdatedAtDesc(pageRequest2Pagable(page, 12));
    List<String> sessionlist = getSessionList();

    searchedDeviceList.forEach(d -> {
      if (sessionlist.contains(d.getDeviceAddr())) {
        d.setConnection(true);
      }
      deviceList.add(d);
    });

    RenderFormWithPage<Devices> result = new RenderFormWithPage<Devices>();
    result.setContent(deviceList);
    result.setNumber(searchedDeviceList.getNumber());
    result.setTotalPages(searchedDeviceList.getTotalPages());
    return result;
  }

  /**
   * get device update status list.
   * 
   * @param page page number
   */
  public RenderFormWithPage<Devices> getDeviceUpdateStatus(int page) throws ParseException {
    List<Devices> deviceList = new ArrayList<Devices>();
    Page<Devices> searchedDeviceList =
        deviceJpaRepository.findAllByOrderByDataUpdatedAtDesc(pageRequest2Pagable(page, 12));

    Calendar serachTime = new GregorianCalendar(Locale.KOREA);
    serachTime.add(Calendar.SECOND, -1 * expiredtime);

    Date validateTime = serachTime.getTime();

    searchedDeviceList.forEach(d -> {
      if (validateTime.before(d.getDataUpdatedAt())) {
        d.setConnection(true);
      }
      deviceList.add(d);
    });

    RenderFormWithPage<Devices> result = new RenderFormWithPage<Devices>();
    result.setContent(deviceList);
    result.setNumber(searchedDeviceList.getNumber());
    result.setTotalPages(searchedDeviceList.getTotalPages());
    return result;
  }

  /**
   * getSessionInfo method.
   * 
   * @param redis redisTemplate
   * @return session list
   */
  private List<String> getSessionList() throws ParseException {
    SessionInfo sessionInfo = null;
    Set<String> keys = redis.keys(sessionPattern);
    ValueOperations<String, Object> vop = redis.opsForValue();
    List<String> validSessionList = new ArrayList<String>();

    for (String k : keys) {
      sessionInfo = (SessionInfo) vop.get(k);

      if (isSessionExpired(sessionInfo.getCreateTime())) {
        logger.debug("[KETI-AUTH-SERVICE] getSessionList() >>>>>> sessionCreateTime:{}",
            sessionInfo.getAddr());
        redis.delete(sessionInfo.getAddr());
        continue;
      }
      validSessionList.add(sessionInfo.getAddr());
    }
    return validSessionList;
  }

  /**
   * session isSessnExpired method.
   * 
   * @param sessionDateTime session date time
   * @return true or false
   * @throws ParseException pasrse expcetion
   */
  public boolean isSessionExpired(String sessionDateTime) throws ParseException {
    // current date time
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String today = formatter.format(new Date());
    Date curDate = formatter.parse(today);

    // create session date time
    Date sessionDate = formatter.parse(sessionDateTime);
    long diff = (curDate.getTime() - sessionDate.getTime()) / 1000;
    logger.debug("[KETI-AUTH-SERVICE] isSessionExpired()  >>>>>> curDate - sessionDate:{} {}",
        diff, "sec");
    boolean result = (diff < expiredtime) ? false : true;
    return result;
  }

  /**
   * getDateTime method.
   * 
   * @return current date time
   * @throws ParseException parse exception
   */
  private String getDateTime() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String curDateTime = dateFormat.format(new Date());
    logger.debug("[KETI-AUTH-SERVICE] getDateTime() >>>>>> : {}", curDateTime);
    return curDateTime;
  }
}
