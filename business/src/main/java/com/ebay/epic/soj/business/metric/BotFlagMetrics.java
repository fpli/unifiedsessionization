package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class BotFlagMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setGuid(null);
  }

  @Override
  public void feed(UniEvent event, UniSessionAccumulator uniSessionAccumulator) {
    RawUniSession uniSession = uniSessionAccumulator.getUniSession();
    EventType eventType = event.getEventType();
    switch (eventType){
      case AUTOTRACK_NATIVE:
      case AUTOTRACK_WEB:
      {
        uniSession.getSurfaceBotList().addAll (event.getBotFlags());
        break;
      }
      case UBI_NONBOT:
      case UBI_BOT:{
        uniSession.getUbiBotList().addAll (event.getBotFlags());
        break;
      }
      case UTP_BOT:
      case UTP_NONBOT:{
        uniSession.getUbiBotList().addAll (event.getBotFlags());
        break;
      }
      default:{
        break;
      }
    }

  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
  }

  @Override
  public void init() throws Exception {
  }
}
