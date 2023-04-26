package com.ebay.epic.soj.business.metric;

import com.ebay.epic.common.model.UniSessionAccumulator;
import com.ebay.epic.common.model.raw.UniEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UniSessionMetrics extends RecordMetrics<UniEvent, UniSessionAccumulator> {

  private static volatile UniSessionMetrics sessionMetrics;

  private UniSessionMetrics() {
    initFieldMetrics();
    try {
      init();
    } catch (Exception e) {
      log.error("Failed to init session metrics", e);
    }
  }

  public static UniSessionMetrics getInstance() {
    if (sessionMetrics == null) {
      synchronized (UniSessionMetrics.class) {
        if (sessionMetrics == null) {
          sessionMetrics = new UniSessionMetrics();
        }
      }
    }
    return sessionMetrics;
  }

  @Override
  public void initFieldMetrics() {
    addFieldMetrics(new CommonMetrics());
    addFieldMetrics(new GlobalSessionIdMetrics());
    addFieldMetrics(new TimestampMetrics());
    addFieldMetrics(new LegacySessionIdMetrics());
    addFieldMetrics(new BotFlagMetrics());
  }
}
