package com.ebay.epic.common;

import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.ArrayList;
import java.util.List;

public class DateTimeUtils {

  public static final long MILLISECONDS_OF_A_DAY = 24 * 60 * 60 * 1000;

  public static boolean isMidnight(long timestamp) {
    return isMidnight(timestamp, 0);
  }

  public static boolean isMidnight(long timestamp, Time offset) {
    return isMidnight(timestamp, offset.toMilliseconds());
  }

  public static boolean isMidnight(long timestamp, long offset) {
    return (timestamp - offset) % MILLISECONDS_OF_A_DAY == 0;
  }

  public static List<Long> midnightsBetween(long start, long end, Time offset) {
    return midnightsBetween(start, end, offset.toMilliseconds());
  }

  public static List<Long> midnightsBetween(long start, long end) {
    return midnightsBetween(start, end, 0);
  }

  /**
   * Calculate midnights inside a window.
   *
   * @param start  The starting timestamp of the window, in milliseconds
   * @param end    The exclusive end timestamp of the window, in milliseconds
   * @param offset The timezone offset
   * @return Midnights inside the window
   */
  public static List<Long> midnightsBetween(long start, long end, long offset) {
    if (start >= end) {
      throw new IllegalArgumentException("Start must be smaller than end.");
    }

    long adjustedStart = start - offset;
    long adjustedEnd = end - offset;

    // Get the first possible midnight
    long remainder = adjustedStart % MILLISECONDS_OF_A_DAY;
    long adjustedMidnight;
    if (remainder == 0) {
      adjustedMidnight = adjustedStart;
    } else {
      adjustedMidnight = adjustedStart - remainder + MILLISECONDS_OF_A_DAY;
    }

    // Get all midnights based on the first possible midnight
    List<Long> midnights = new ArrayList<>();
    while (adjustedMidnight < adjustedEnd) {
      midnights.add(adjustedMidnight + offset);
      adjustedMidnight = adjustedMidnight + MILLISECONDS_OF_A_DAY;
    }

    return midnights;
  }
}
