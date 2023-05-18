package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.LkpManager;

import java.util.Map;

public class Gr1CntMetrics extends ClavSessionFieldMetrics {

  public static final String GR_1="GR-1";
  @Override
  public void process(UniEvent event, ClavSession clavSession) throws Exception {
    Map<Integer, String[]> pageFmlyNameMap = LkpManager.getInstance().getPageFmlyMaps();
    Integer pageId = event.getPageId();
    String[] pageFmlyName = pageFmlyNameMap.get(pageId);
    if (event.getRdt()==0
            && !event.getIframe()
            && event.isClavValidPage()
            && (pageFmlyName != null && GR_1.equals(pageFmlyName[1]))) {
      clavSession.setGr1Count(clavSession.getGr1Count() + 1);
    }
  }

}
