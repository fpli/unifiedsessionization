package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.business.metric.FieldMetrics;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.IsValidIPv4;
import com.ebay.sojourner.common.util.Property;
import com.ebay.sojourner.common.util.PropertyUtils;
import com.ebay.sojourner.common.util.UBIConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class ClavAgentStringMetrics implements FieldMetrics<UniEvent, ClavSession> {

    public static final String SHOCKWAVE_FLASH_AGENT = "Shockwave Flash";
    public static final int AGENT_MAX_LENGTH = 2000;
    private static Set<Integer> agentExcludeSet;

    @Override
    public void init() throws Exception {
        agentExcludeSet = PropertyUtils.getIntegerSet(
                UBIConfig.getString(Property.AGENT_EXCLUDE_PAGES), Property.PROPERTY_DELIMITER);
        log.info("UBIConfig.getString(Property.AGENT_EXCLUDE_PAGES): {}",
                 UBIConfig.getString(Property.AGENT_EXCLUDE_PAGES));
    }

    @Override
    public void start(ClavSession clavSession) throws Exception {
        // do clear first as end method may not been invoked.
        clavSession.setUserAgent(null);
    }


    @Override
    public void process(UniEvent event, ClavSession clavSession) throws Exception {
        String agentInfo = event.getUserAgent();
        boolean isEarlyValidEvent = com.ebay.epic.soj.common.utils.SojEventTimeUtil
                .isEarlyEvent(event.getEventTs(), clavSession.getStartTimestamp());

        if (!event.getIframe()
                && (event.getRdt() == 0)
                && !agentExcludeSet.contains(event.getPageId())
                && agentInfo != null
                && !agentInfo.equals(SHOCKWAVE_FLASH_AGENT)
                && !IsValidIPv4.isValidIP(agentInfo)) {
            if (agentInfo.length() > AGENT_MAX_LENGTH) {
                agentInfo = agentInfo.substring(0, AGENT_MAX_LENGTH);
            }
            if (isEarlyValidEvent) {
                clavSession.setUserAgent(agentInfo);
            } else {
                if (clavSession.getUserAgent() == null) {
                    clavSession.setUserAgent(agentInfo);
                }
            }
        }
    }

}
