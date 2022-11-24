package com.ebay.epic.business.metric;

import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;
import com.ebay.epic.common.model.raw.UniEvent;

public class CommonMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setGuid(null);
  }

  @Override
  public void feed(UniEvent event, UniSessionAccumulator uniSessionAccumulator) {
    RawUniSession uniSession = uniSessionAccumulator.getUniSession();
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
