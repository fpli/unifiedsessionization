package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.lookup.PageFamilyInfo;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceLookupManager;
import com.ebay.sojourner.common.util.Property;
import com.ebay.sojourner.common.util.PropertyUtils;
import com.ebay.sojourner.common.util.UBIConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViCntMetrics extends ClavSessionFieldMetrics {

    public static final String VI = "VI";
    public static final String GRVI = "GR/VI";
    public static final List<String> list = Arrays.asList(VI, GRVI);
    public static final String PGT = "pgt";
    private List<String> viPGT;
    private TrafficSourceLookupManager lookupManager;

    @Override
    public void init() throws Exception {
        viPGT = new ArrayList<>(PropertyUtils.parseProperty(
                UBIConfig.getString(Property.VI_EVENT_VALUES), Property.PROPERTY_DELIMITER));
        lookupManager = TrafficSourceLookupManager.getInstance();
    }

    @Override
    public void process(UniEvent event, ClavSession clavSession) throws Exception {
        Integer pageId = event.getPageId();
        PageFamilyInfo pageFamilyInfo = lookupManager.getPageFamilyAllMap().get(pageId);
        if (event.getRdt() == 0
                && event.isPartialValidPage()
                && ((pageFamilyInfo != null && list.contains(pageFamilyInfo.getPageFamily4())) || VI.equals(getImPGT(event)))) {
            clavSession.setViCount(clavSession.getViCount() + 1);
        }
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
