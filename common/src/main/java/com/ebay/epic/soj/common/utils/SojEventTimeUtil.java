package com.ebay.epic.soj.common.utils;

public class SojEventTimeUtil {

  public static boolean isEarlyEvent(Long eventimestamp, Long absEventimestamp) {
    if (eventimestamp == null) {
      return false;
    } else if (absEventimestamp == null) {
      return true;
    }
    return eventimestamp < absEventimestamp;
  }

  public static boolean isLateEvent(Long eventimestamp, Long absEventimestamp) {
    if (eventimestamp == null) {
      return false;
    } else if (absEventimestamp == null) {
      return true;
    }
    return eventimestamp > absEventimestamp;
  }
}
