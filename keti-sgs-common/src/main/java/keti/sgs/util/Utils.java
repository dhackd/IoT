package keti.sgs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Utils {
  public static Date string2startDate(String date) throws ParseException {
    return new SimpleDateFormat("MM/dd/yyyy").parse(date);
  }

  /**
   * set time 23:59:59.
   * 
   * @param date date
   */
  public static Date string2endDate(String date) throws ParseException {
    Date time = new SimpleDateFormat("MM/dd/yyyy").parse(date);
    Calendar cal = Calendar.getInstance();
    cal.setTime(time);
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.SECOND, -1);
    return cal.getTime();
  }

  public static Pageable pageRequest2Pagable(int page) {
    return PageRequest.of(page, 10);
  }
  
  public static Pageable pageRequest2Pagable(int page, int count) {
    return PageRequest.of(page, count);
  }
  
  /**
   * random key gen.
   * 
   * @param count count
   * @return random value
   */
  public static String randomKeyGenerator(int count) {
    return RandomStringUtils.randomAlphanumeric(count);
  }
}
