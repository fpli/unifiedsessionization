package com.ebay.epic.business.metric;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.UniSessionAccumulator;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.ebay.epic.common.model.raw.UniEvent;

import java.util.concurrent.CopyOnWriteArraySet;

public class LegacySessionIdMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setUbiSessIds(new CopyOnWriteArraySet<>());
    uniSessionAccumulator.getUniSession().setUbiSessSkeys(new CopyOnWriteArraySet<>());
    uniSessionAccumulator.getUniSession().setAutotrackSessIds(new CopyOnWriteArraySet<>());
    uniSessionAccumulator.getUniSession().setAutotrackSessSkeys(new CopyOnWriteArraySet<>());
  }

  @Override
  public void feed(UniEvent event, UniSessionAccumulator uniSessionAccumulator) {
    RawUniSession rawUniSession = uniSessionAccumulator.getUniSession();
    if(event.getEventType()== EventType.AUTOTRACK_WEB||event.getEventType()== EventType.AUTOTRACK_NATIVE){
      rawUniSession.getAutotrackSessIds().add(Long.valueOf(event.getSessionId()));
      rawUniSession.getAutotrackSessSkeys().add(event.getSessionSkey());
    }else if(event.getEventType()==EventType.UBI_BOT||event.getEventType()==EventType.UBI_NONBOT){
      rawUniSession.getUbiSessIds().add(event.getSessionId());
      rawUniSession.getUbiSessSkeys().add(event.getSessionSkey());
    }

  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
  }

  @Override
  public void init() throws Exception {
  }
}
