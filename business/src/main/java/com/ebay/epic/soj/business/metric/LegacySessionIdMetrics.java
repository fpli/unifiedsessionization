package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

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
  public void process(UniEvent event, UniSessionAccumulator uniSessionAccumulator) throws Exception {
    RawUniSession rawUniSession = uniSessionAccumulator.getUniSession();
    if(event.getEventType()== EventType.AUTOTRACK_WEB||event.getEventType()== EventType.AUTOTRACK_NATIVE){
      rawUniSession.getAutotrackSessIds().add(Long.valueOf(event.getSessionId()));
      rawUniSession.getAutotrackSessSkeys().add(event.getSessionSkey());
    }else if(event.getEventType()==EventType.UBI_BOT||event.getEventType()==EventType.UBI_NONBOT){
      rawUniSession.getUbiSessIds().add(event.getSessionId());
      rawUniSession.getUbiSessSkeys().add(event.getSessionSkey());
    }
  }
}
