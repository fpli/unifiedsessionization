package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class CommonMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setGuid(null);
  }

  @Override
  public void process(UniEvent event, UniSessionAccumulator uniSessionAccumulator) throws Exception {
    RawUniSession uniSession = uniSessionAccumulator.getUniSession();
    if (uniSession.getGuid() == null) {
      uniSession.setGuid(event.getGuid());
    }else if(event.getGuid()==null){
      event.setGuid(uniSession.getGuid());
    }
  }

}
