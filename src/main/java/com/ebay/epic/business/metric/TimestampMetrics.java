package com.ebay.epic.business.metric;

import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;

public class TimestampMetrics implements FieldMetrics<RawEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setAbsStartTimestamp(null);
    uniSessionAccumulator.getUniSession().setAbsEndTimestamp(null);
  }

  @Override
  public void feed(RawEvent event, UniSessionAccumulator uniSessionAccumulator) {
    UniSession uniSession = uniSessionAccumulator.getUniSession();
    if (uniSession.getAbsStartTimestamp() == null) {
      uniSession.setAbsStartTimestamp(event.getEventTs());
    } else if (event.getEventTs() != null
              && uniSession.getAbsStartTimestamp() > event.getEventTs()) {
      uniSession.setAbsStartTimestamp(event.getEventTs());
    }

    if (uniSession.getAbsEndTimestamp() == null) {
      uniSession.setAbsEndTimestamp(event.getEventTs());
    } else if (event.getEventTs() != null
              && uniSession.getAbsEndTimestamp() < event.getEventTs()) {
      uniSession.setAbsEndTimestamp(event.getEventTs());
    }

  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession()
                      .setSessionStartDt(uniSessionAccumulator.getUniSession()
                                                      .getAbsStartTimestamp());
  }

  @Override
  public void init() throws Exception {
  }
}
