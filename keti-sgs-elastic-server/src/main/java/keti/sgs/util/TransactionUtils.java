package keti.sgs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransactionUtils {

  /**
   * check if date exists.
   */
  public static boolean checkDate(String start, String end) {
    if (start.equals("0") && end.equals("0")) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * convert string start date to long start time.
   */
  public static long stringStartDateToStartTime(String start) throws ParseException {
    return new SimpleDateFormat("MM/dd/yyyy").parse(start).getTime();
  }

  /**
   * convert string end date to long end time.
   */
  public static long stringEndDateToEndTime(String end) throws ParseException {
    Date date = new SimpleDateFormat("MM/dd/yyyy").parse(end);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, 1);
    calendar.add(Calendar.SECOND, -1);
    return calendar.getTime().getTime();
  }
}
