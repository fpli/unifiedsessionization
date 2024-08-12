package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class SessionCntMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  @Override
  public void start(UniSessionAccumulator uniSessionAccumulator) {
    uniSessionAccumulator.getUniSession().setEventCnt(0);
    uniSessionAccumulator.getUniSession().setUbiCnt(0);
    uniSessionAccumulator.getUniSession().setUtpCnt(0);
    uniSessionAccumulator.getUniSession().setSurfaceCnt(0);
    uniSessionAccumulator.getUniSession().setRoiCnt(0);
  }

  @Override
  public void process(UniEvent uniEvent, UniSessionAccumulator uniSessionAccumulator) throws Exception {
    RawUniSession session = uniSessionAccumulator.getUniSession();
    session.setEventCnt(session.getEventCnt() + 1);
    switch (uniEvent.getEventType()) {
      case UBI_NONBOT:
      case UBI_BOT:
        session.setUbiCnt(session.getUbiCnt() + 1);
        break;
      case UTP_NONBOT:
      case UTP_BOT:
        session.setUtpCnt(session.getUtpCnt() + 1);
        break;
      case AUTOTRACK_WEB:
      case AUTOTRACK_NATIVE:
        session.setSurfaceCnt(session.getSurfaceCnt() + 1);
        break;
      case ROI_NONBOT:
        session.setRoiCnt(session.getRoiCnt() + 1);
        break;
      default:
        break;
    }

  }

}
