package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.utils.SojEventTimeUtil;

public class UbiGeneralMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setFirstAppId(null);
    uniSessionAccumulator.getUniSession().setAppId(null);
    uniSessionAccumulator.getUniSession().setFirstCobrand(Integer.MIN_VALUE);
    uniSessionAccumulator.getUniSession().setCobrand(Integer.MIN_VALUE);
  }

  @Override
  public boolean accept(UniEvent uniEvent) {
    return EventType.UBI_BOT.equals(uniEvent.getEventType())
            || EventType.UBI_NONBOT.equals(uniEvent.getEventType());
  }

  @Override
  public void process(UniEvent event, UniSessionAccumulator uniSessionAccumulator) throws Exception {
    RawUniSession uniSession = uniSessionAccumulator.getUniSession();
    boolean isEarlyEvent = SojEventTimeUtil
            .isEarlyEvent(event.getEventTs(),
                    uniSessionAccumulator.getUniSession().getAbsStartTimestamp());
    boolean isEarlyValidEvent = SojEventTimeUtil
            .isEarlyEvent(event.getEventTs(),
                    uniSessionAccumulator.getUniSession().getStartTimestamp());

    // first app id
    if ((isEarlyEvent ? isEarlyEvent : uniSessionAccumulator.getUniSession().getFirstAppId() == null)
            && event.getAppId() != null) {
      uniSessionAccumulator.getUniSession().setFirstAppId(Integer.valueOf(event.getAppId()));
    }
    if ((isEarlyValidEvent ? isEarlyValidEvent
            : uniSessionAccumulator.getUniSession().getAppId() == null)
            && !event.getIframe()
            && (event.getRdt()==0)
            && event.getAppId() != null) {
      uniSessionAccumulator.getUniSession().setAppId(Integer.valueOf(event.getAppId()));
    }

    // cobrand
    if (uniSessionAccumulator.getUniSession().getCobrand() == Integer.MIN_VALUE
            && !event.getIframe()
            && (event.getRdt()==0)
            && event.isClavValidPage()) {
      uniSessionAccumulator.getUniSession().setCobrand(Integer.valueOf(event.getCobrand()));
    }
    if (uniSessionAccumulator.getUniSession().getFirstCobrand() == Integer.MIN_VALUE) {
      uniSessionAccumulator.getUniSession().setFirstCobrand(Integer.valueOf(event.getCobrand()));
    }
  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
    if(uniSessionAccumulator.getUniSession().getAppId()!=null) {
      uniSessionAccumulator.getUniSession()
              .setFirstAppId(uniSessionAccumulator.getUniSession().getAppId());
    }
    if (uniSessionAccumulator.getUniSession().getCobrand() == Integer.MIN_VALUE) {
      uniSessionAccumulator.getUniSession()
              .setCobrand(uniSessionAccumulator.getUniSession().getFirstCobrand());
    }
  }

}
