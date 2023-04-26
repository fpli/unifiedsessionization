package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.utils.SojEventTimeUtil;

public class TimestampMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setAbsStartTimestamp(null);
    uniSessionAccumulator.getUniSession().setAbsEndTimestamp(null);
    uniSessionAccumulator.getUniSession().setStartTimestamp(null);
  }

  @Override
  public void feed(UniEvent event, UniSessionAccumulator uniSessionAccumulator) {
    RawUniSession uniSession = uniSessionAccumulator.getUniSession();
    boolean isEarlyValidEvent = SojEventTimeUtil
            .isEarlyEvent(event.getEventTs(),
                    uniSessionAccumulator.getUniSession().getStartTimestamp());
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
    if(isEarlyValidEvent&&!event.getIframe()&&event.getRdt()==0){
      uniSession.setStartTimestamp(event.getEventTs());
    }

  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
    if(uniSessionAccumulator.getUniSession().getStartTimestamp()!=null) {
      uniSessionAccumulator.getUniSession()
              .setSessionStartDt(uniSessionAccumulator.getUniSession()
                      .getStartTimestamp());
    }else{
      uniSessionAccumulator.getUniSession()
              .setSessionStartDt(uniSessionAccumulator.getUniSession()
                      .getAbsStartTimestamp());
    }
  }

  @Override
  public void init() throws Exception {
  }
}
