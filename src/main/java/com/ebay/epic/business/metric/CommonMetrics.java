package com.ebay.epic.business.metric;

import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;

public class CommonMetrics implements FieldMetrics<RawEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setGuid(null);
  }

  @Override
  public void feed(RawEvent event, UniSessionAccumulator uniSessionAccumulator) {
    UniSession uniSession = uniSessionAccumulator.getUniSession();
    if (uniSession.getGuid() == null) {
      uniSession.setGuid(event.getGuid());
    }else if(event.getGuid()==null){
      event.setGuid(uniSession.getGuid());
    }
  }

  @Override
  public void end(UniSessionAccumulator uniSessionAccumulator) {
  }

  @Override
  public void init() throws Exception {
  }
}
