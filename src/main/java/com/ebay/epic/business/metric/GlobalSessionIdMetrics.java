package com.ebay.epic.business.metric;

import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;

public class GlobalSessionIdMetrics implements FieldMetrics<RawEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setGlobalSessionId(null);
  }

  @Override
  public void feed(RawEvent event, UniSessionAccumulator uniSessionAccumulator) {
    UniSession uniSession = uniSessionAccumulator.getUniSession();
    if (!event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      uniSession.setGlobalSessionId(event.getSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() != null) {
      event.setSessionId(uniSession.getGlobalSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      event.updateSessionId();
      uniSession.setGlobalSessionId(event.getSessionId());
    }
  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
  }

  @Override
  public void init() throws Exception {
  }
}
