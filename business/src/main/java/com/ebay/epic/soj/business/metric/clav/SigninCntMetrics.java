package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.LkpManager;
import com.google.common.collect.ImmutableSet;

import java.util.Map;

public class SigninCntMetrics extends ClavSessionFieldMetrics {

  private static final String SIGN_IN = "SIGNIN";
  @Override
  public void process(UniEvent event, ClavSession clavSession) throws Exception {
    Map<Integer, String[]> pageFmlyNameMap = LkpManager.getInstance().getPageFmlyMaps();
    Integer pageId = event.getPageId();
    String[] pageFmlyName = pageFmlyNameMap.get(pageId);
    if (event.getRdt()==0
            && !event.getIframe()
            && event.isClavValidPage()
            && (pageFmlyName != null && SIGN_IN.equals(pageFmlyName[1]))) {
      clavSession.setSigninCount(clavSession.getSigninCount() + 1);
    }
  }

}
