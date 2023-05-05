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

public class GrCntMetrics extends ClavSessionFieldMetrics {
    public static final String GR = "GR";
    public static final String PGT = "pgt";
    public static final String VI = "vi";
    private List<String> viPGT;

    @Override
    public void process(UniEvent event, ClavSession clavSession) throws Exception {
        Map<Integer, String[]> pageFmlyNameMap = LkpManager.getInstance().getPageFmlyMaps();
        Integer pageId = event.getPageId();
        if (event.getRdt() == 0
                && !event.getIframe()
                && event.isPartialValidPage()
                && pageId != -1
                && ((pageFmlyNameMap.containsKey(pageId) && GR.equals(pageFmlyNameMap.get(pageId)[1]))
                || (getImPGT(event) != null && GR.equals(getImPGT(event))))) {
            clavSession.setGrCount(clavSession.getGrCount() + 1);
        }
    }

    @Override
    public void init() throws Exception {
        viPGT = new ArrayList<>(PropertyUtils.parseProperty(
                UBIConfig.getString(Property.VI_EVENT_VALUES), Property.PROPERTY_DELIMITER));
    }

    private String getImPGT(UniEvent event) {
        if (event.getPageId() == 1521826
                && StringUtils.isNotBlank(event.getPayload().get(PGT))
                && viPGT.contains(event.getPayload().get(PGT))) {
            return VI;
        }
        if (event.getPageId() == 2066804
                && StringUtils.isNotBlank(event.getPageUrl())
                && (event.getPageUrl().startsWith("/itm/like")
                || event.getPageUrl().startsWith("/itm/future"))) {
            return "VI";
        }
        if (event.getPageId() == 1521826 || event.getPageId() == 2066804) {
            return "GR";
        }
        return null;
    }
}
