package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.business.metric.FieldMetrics;
import com.ebay.epic.soj.common.dds.UAParserFactory;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.platform.raptor.raptordds.model.UserAgentInfo;
import com.ebay.platform.raptor.raptordds.parsers.UserAgentParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExperienceAndDeviceMetrics implements FieldMetrics<UniEvent, ClavSession> {

    private transient UserAgentParser uaParser;

    @Override
    public void init() throws Exception {
        uaParser = UAParserFactory.getInstance();
    }

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        String userAgent = clavSession.getUserAgent();

        if (clavSession.getUserAgent() != null) {
            UserAgentInfo uaInfo = uaParser.parse(userAgent);

            if (uaInfo.getDeviceInfo().getBrowser() != null
                    && (uaInfo.isDesktop() || uaInfo.isTablet())) {
                clavSession.setExperienceLevel2("Browser: Core site");
                clavSession.setDeviceTypeLevel2("PC");
            } else if (uaInfo.getDeviceInfo().getBrowser() != null
                    && uaInfo.isMobile()) {
                clavSession.setExperienceLevel2("Browser: mWeb");
                clavSession.setDeviceTypeLevel2("Mobile: Phone");
            } else if (uaInfo.getAppInfo() != null
                    && uaInfo.getAppInfo().getAppName() != null
                    && "Android".equalsIgnoreCase(uaInfo.getDeviceInfo().getDeviceOS())) {
                clavSession.setExperienceLevel2("Apps: Android");
                clavSession.setDeviceTypeLevel2("Mobile: Phone");
            } else if (uaInfo.getAppInfo() != null
                    && uaInfo.getAppInfo().getAppName() != null
                    && "iOS".equalsIgnoreCase(uaInfo.getDeviceInfo().getDeviceOS())
                    && uaInfo.getDeviceInfo().getModel() != null
                    && uaInfo.getDeviceInfo().getModel().toLowerCase().contains("iphone")) {
                clavSession.setExperienceLevel2("Apps: iPhone");
                clavSession.setDeviceTypeLevel2("Mobile: Phone");
            } else if (uaInfo.getAppInfo() != null
                    && uaInfo.getAppInfo().getAppName() != null
                    && "iOS".equalsIgnoreCase(uaInfo.getDeviceInfo().getDeviceOS())
                    && uaInfo.getDeviceInfo().getModel() != null
                    && uaInfo.getDeviceInfo().getModel().toLowerCase().contains("ipad")) {
                clavSession.setExperienceLevel2("Apps: iPad");
                clavSession.setDeviceTypeLevel2("Mobile: Tablet");
            } else if (uaInfo.getAppInfo() != null
                    && uaInfo.getAppInfo().getAppName() != null) {
                clavSession.setExperienceLevel2("Apps: Other");
                clavSession.setDeviceTypeLevel2("Apps: Other");
            }
        }
    }
}
