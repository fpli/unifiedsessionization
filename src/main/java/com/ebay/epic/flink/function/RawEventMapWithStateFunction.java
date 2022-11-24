package com.ebay.epic.flink.function;

import com.ebay.epic.common.model.UniSessionAccumulator;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.ebay.epic.common.model.raw.UniEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.runtime.operators.windowing.MapWithStateFunction;

@Slf4j
public class RawEventMapWithStateFunction
    implements MapWithStateFunction<UniEvent, UniSessionAccumulator, UniEvent> {

  @Override
  public UniEvent map(UniEvent event, UniSessionAccumulator sessionAccumulator) throws Exception {
    RawUniSession uniSession = new RawUniSession();
    if (!event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      uniSession.setGlobalSessionId(event.getGlobalSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() != null) {
      event.setGlobalSessionId(uniSession.getGlobalSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      log.error("unievent update in RawEventMapWithStateFunction=====");
      event.updateGlobalSessionId();
      uniSession.setGlobalSessionId(event.getGlobalSessionId());
    }
    return event;
  }
}
