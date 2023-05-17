package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.LkpManager;

import java.util.Map;

public class BotMetrics extends ClavSessionFieldMetrics {

  public static final String GR_1="GR-1";
  @Override
  public void process(UniEvent event, ClavSession clavSession) throws Exception {
    for(Integer botFlag :event.getBotFlags()) {
      clavSession.setBotFlag(botFlag!=0 ? 1L : clavSession.getBotFlag());
    }
  }
}
