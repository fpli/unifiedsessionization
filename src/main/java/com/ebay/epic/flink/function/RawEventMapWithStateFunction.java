package com.ebay.epic.flink.function;

import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.runtime.operators.windowing.MapWithStateFunction;

@Slf4j
public class RawEventMapWithStateFunction
    implements MapWithStateFunction<RawEvent, UniSessionAccumulator, RawEvent> {

  @Override
  public RawEvent map(RawEvent event, UniSessionAccumulator sessionAccumulator) throws Exception {
    UniSession uniSession = new UniSession();
    if (!event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      uniSession.setGlobalSessionId(event.getSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() != null) {
      event.setSessionId(uniSession.getGlobalSessionId());
    } else if (event.isNewSession() && uniSession.getGlobalSessionId() == null) {
      event.updateSessionId();
      uniSession.setGlobalSessionId(event.getSessionId());
    }
    return event;
  }
}
