package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.LkpManager;
import com.ebay.sojourner.common.util.Property;
import com.ebay.sojourner.common.util.PropertyUtils;
import com.ebay.sojourner.common.util.UBIConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomepageCntMetrics extends ClavSessionFieldMetrics {

    public static final String HOME = "HOME";
    private List<String> viPGT;

    @Override
    public void process(UniEvent event, ClavSession clavSession) throws Exception {
        Map<Integer, String[]> pageFmlyNameMap = LkpManager.getInstance().getPageFmlyMaps();
        Integer pageId = event.getPageId();
        if (event.getRdt() == 0
                && !event.getIframe()
                && event.isClavValidPage()
                && pageId != -1
                && pageFmlyNameMap.containsKey(pageId)
                && HOME.equals(pageFmlyNameMap.get(pageId)[1])) {
            clavSession.setHomepageCount(clavSession.getHomepageCount() + 1);
        }
    }
}
