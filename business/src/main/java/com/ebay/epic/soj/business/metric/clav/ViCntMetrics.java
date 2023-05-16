package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.LkpManager;

import java.util.Map;

public class ViCntMetrics extends ClavSessionFieldMetrics {

  public static final String VI="VI";
  @Override
  public void process(UniEvent event, ClavSession clavSession) throws Exception {
    Map<Integer, String[]> pageFmlyNameMap = LkpManager.getInstance().getPageFmlyMaps();
    Integer pageId = event.getPageId();
    String[] pageFmlyName = pageFmlyNameMap.get(pageId);
    if (event.getRdt()==0
            && !event.getIframe()
            && event.isClavValidPage()
            && (pageFmlyName != null && VI.equals(pageFmlyName[1]))) {
      clavSession.setViCount(clavSession.getViCount() + 1);
    }
  }

}
