package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class LegacySessMetrics extends ClavSessionFieldMetrics {

  public static final String GR_1="GR-1";
  public static final String SESSION_SKEY="session_skey";
  @Override
  public void process(UniEvent event, ClavSession clavSession) throws Exception {
    // only keep ubi sessionskey
    if(event.getSessionSkey() != null) {
      clavSession.getOthers().put(SESSION_SKEY, event.getSessionSkey().toString());
    }
  }
}
