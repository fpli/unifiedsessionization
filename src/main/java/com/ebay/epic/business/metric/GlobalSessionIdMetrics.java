package com.ebay.epic.business.metric;

import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;

public class GlobalSessionIdMetrics implements FieldMetrics<RawEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setGlobalSessionId(null);
  }

  @Override
  public void feed(RawEvent event, UniSessionAccumulator uniSessionAccumulator) {
    RawUniSession uniSession = uniSessionAccumulator.getUniSession();
    if (!event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      uniSession.setGlobalSessionId(event.getGlobalSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() != null) {
      event.setGlobalSessionId(uniSession.getGlobalSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      event.updateGlobalSessionId();
      uniSession.setGlobalSessionId(event.getGlobalSessionId());
    }
  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
  }

  @Override
  public void init() throws Exception {
  }
}
