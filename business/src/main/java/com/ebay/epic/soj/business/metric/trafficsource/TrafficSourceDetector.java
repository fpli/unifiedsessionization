package com.ebay.epic.soj.business.metric.trafficsource;

import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.*;
import com.ebay.epic.soj.common.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.ebay.epic.soj.common.enums.EventType.*;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.*;

public class TrafficSourceDetector {
    public TrafficSourceCandidate extractCandidate(UniEvent uniEvent) {
        TrafficSourceLookupManager lookupManager = TrafficSourceLookupManager.getInstance();
        int pageId = uniEvent.getPageId();
        // UBI event
        if (UBI_BOT.equals(uniEvent.getEventType()) || UBI_NONBOT.equals(uniEvent.getEventType())) {
            // Chocolate event
            if (pageId == CHOCOLATE_PAGE) {
                UtpEvent event = new UtpEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setChnl(Integer.parseInt(uniEvent.getPayload().get(PAYLOAD_KEY_CHNL)));
                long rotid = Long.parseLong(uniEvent.getPayload().get(PAYLOAD_KEY_ROTID));
                event.setRotid(rotid);
                event.setMpxChnlId(lookupManager.getDwMpxRotationMap().get(rotid).getMpxChnlId());
                event.setUrl(UrlUtils.decode(uniEvent.getPayload().get(PAYLOAD_KEY_URL_MPRE)));
                event.setPageId(pageId);
                return event;
            }
            // Notification event
            else if (pageId == NOTIFICATION_PAGE && "1".equals(uniEvent.getPayload().get(PAYLOAD_KEY_PNACT))) {
                UtpEvent event = new UtpEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setPageId(pageId);
                return event;
            }
            // IMBD event
            else if (pageId == IMBD_PAGE) {
                ImbdEvent event = new ImbdEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setMppid(uniEvent.getPayload().get(PAYLOAD_KEY_MPPID));
                return event;
            }
            // Deeplink event
            else if (pageId == DEEPLINK_PAGE && !uniEvent.getPayload().get(PAYLOAD_KEY_REF).isEmpty()
                    && !"unknown".equals(uniEvent.getPayload().get(PAYLOAD_KEY_REF))) {
                DeeplinkActionEvent event = new DeeplinkActionEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setReferer(uniEvent.getPayload().get(PAYLOAD_KEY_REF));
                return event;
            }
            // UBI valid event
            else if (lookupManager.getPageMap().get(uniEvent.getPageId()).getIframe() == 0
                    && uniEvent.getRdt() == 0) {
                ValidUbiEvent event = new ValidUbiEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setPageId(uniEvent.getPageId());
                event.setPageName(lookupManager.getPageMap().get(uniEvent.getPageId()).getPageName());
                event.setUrl(uniEvent.getPageUrl());
                if (!uniEvent.getReferer().isEmpty()) {
                    event.setReferer(uniEvent.getReferer());
                } else {
                    event.setReferer(uniEvent.getPayload().get(PAYLOAD_KEY_REF));
                }
                return event;
            }
        }

        // Surface valid event, only need web events
        if (AUTOTRACK_WEB.equals(uniEvent.getEventType())
                && lookupManager.getPageMap().get(uniEvent.getPageId()).getIframe() == 0) {
            ValidSurfaceEvent event = new ValidSurfaceEvent();
            event.setEventTimestamp(uniEvent.getEventTs());
            event.setPageId(uniEvent.getPageId());
            event.setPageName(lookupManager.getPageMap().get(uniEvent.getPageId()).getPageName());
            event.setUrl(uniEvent.getPageUrl());
            event.setReferer(uniEvent.getReferer());
            return event;
        }

        return null;
    }

    public TrafficSourceDetails determineTrafficSource(TrafficSourceCandidates trafficSourceCandidates) {
        TrafficSourceDetails details = new TrafficSourceDetails();

        // first valid event
        long firstEventTs = -1;
        String firstEventUrl = "";
        String firstEventRef = "";
        String firstEventPageName = "";
        int firstEventPageId = -1;
        ValidSurfaceEvent surfaceEvent = trafficSourceCandidates.getFirstValidSurfaceEvent();
        ValidUbiEvent ubiEvent = trafficSourceCandidates.getFirstValidUbiEvent();
        if (ubiEvent != null && surfaceEvent == null) {
            firstEventTs = ubiEvent.getEventTimestamp();
            firstEventUrl = ubiEvent.getUrl();
            firstEventRef = ubiEvent.getReferer();
            firstEventPageName = ubiEvent.getPageName();
            firstEventPageId = ubiEvent.getPageId();
        } else if (ubiEvent == null && surfaceEvent != null) {
            firstEventTs = surfaceEvent.getEventTimestamp();
            firstEventUrl = surfaceEvent.getUrl();
            firstEventRef = surfaceEvent.getReferer();
            firstEventPageName = surfaceEvent.getPageName();
            firstEventPageId = surfaceEvent.getPageId();
        } else if (ubiEvent != null && surfaceEvent != null) {
            long ubiTs = ubiEvent.getEventTimestamp();
            long surfaceTs = surfaceEvent.getEventTimestamp();
            if (ubiTs <= surfaceTs) {
                firstEventTs = ubiTs;
                firstEventUrl = ubiEvent.getUrl();
                firstEventRef = ubiEvent.getReferer();
                firstEventPageName = ubiEvent.getPageName();
                firstEventPageId = ubiEvent.getPageId();
            } else {
                firstEventTs = surfaceTs;
                firstEventUrl = surfaceEvent.getUrl();
                firstEventRef = surfaceEvent.getReferer();
                firstEventPageName = surfaceEvent.getPageName();
                firstEventPageId = surfaceEvent.getPageId();
            }
        }

        // fallback referer
        if (StringUtils.isEmpty(firstEventRef) && trafficSourceCandidates.getFirstDeeplinkActionEvent() != null) {
            firstEventRef = trafficSourceCandidates.getFirstDeeplinkActionEvent().getReferer();
        }

        // set common fields
        details.setLdngPageId(firstEventPageId);
        details.setReferer(UrlUtils.getDomain(firstEventRef));

        // UTP traffic source, including Chocolate and Notification
        // TODO: split Chocolate and Notification
        if (trafficSourceCandidates.getFirstUtpEvent() != null
                && Math.abs(trafficSourceCandidates.getFirstUtpEvent().getEventTimestamp() - firstEventTs)
                <= UTP_THRESHOLD) {
            UtpEvent utpEvent = trafficSourceCandidates.getFirstUtpEvent();
            int pageId = utpEvent.getPageId();

            // Notification
            if (pageId == NOTIFICATION_PAGE) {
                details.setTrafficSourceLevel3(NOTIFICATIONS_APPS);
                return details;
            }

            // Chocolate
            int mpxChnlId = utpEvent.getMpxChnlId();
            int chnl = utpEvent.getChnl();
            String url = utpEvent.getUrl();
            details = chocolateTrafficSource(mpxChnlId, chnl, url, utpEvent.getRotid());
            if (StringUtils.isNotEmpty(details.getTrafficSourceLevel3())) {
                return details;
            }
        }

        // fallback Chocolate traffic source using first valid event
        String mkcid = UrlUtils.getParamValue(firstEventUrl, "mkcid");
        if (StringUtils.isNotEmpty(mkcid) && CHOCOLATE_CHNL.contains(mkcid)) {
            long rotId = -1;
            int mpxChnlId = -1;
            int chnl = Integer.parseInt(mkcid);;
            String mkrid = UrlUtils.getParamValue(firstEventUrl, "mkrid");
            if (StringUtils.isNotEmpty(mkrid)) {
                rotId = Long.parseLong(mkrid);
                mpxChnlId = TrafficSourceLookupManager.getInstance().getDwMpxRotationMap().get(rotId).getMpxChnlId();
            }
            details = chocolateTrafficSource(mpxChnlId, chnl, firstEventUrl, rotId);
            if (StringUtils.isNotEmpty(details.getTrafficSourceLevel3())) {
                return details;
            }
        }

        // IMBD traffic source
        if (trafficSourceCandidates.getFirstImbdEvent() != null
                && Math.abs(trafficSourceCandidates.getFirstImbdEvent().getEventTimestamp() - firstEventTs)
                <= IMBD_THRESHOLD) {
            details.setTrafficSourceLevel3(ORGANIC_IMBD);
            details.setMppid(trafficSourceCandidates.getFirstImbdEvent().getMppid());
            return details;
        }

        // referer based traffic source
        if (containsKey(firstEventRef, SEARCH_ENGINE_KEYWORD) && !firstEventRef.contains(MAIL_KEYWORD)) {
            if (!HOME_PAGE_NAME.equals(firstEventPageName)) {
                details.setTrafficSourceLevel3(FREE_SEO_NATURAL_SEARCH);
            } else {
                details.setTrafficSourceLevel3(ORGANIC_NAV_SEARCH_FREE);
            }
        } else if (firstEventRef.contains(EBAY_KEYWORD)) {
            details.setTrafficSourceLevel3(ORGANIC_DIRECT_ON_EBAY);
        } else if (containsKey(firstEventRef, SOCIAL_KEYWORD)) {
            details.setTrafficSourceLevel3(FREE_FREE_SOCIAL);
        } else if (firstEventRef.contains(MAIL_KEYWORD)) {
            details.setTrafficSourceLevel3(ORGANIC_TXN_COMMS_WEBMAIL);
        } else if (StringUtils.isEmpty(firstEventRef) || firstEventRef == NULL_KEYWORD) {
            details.setTrafficSourceLevel3(ORGANIC_DIRECT_NO_REF);
        } else {
            details.setTrafficSourceLevel3(FREE_OTHER);
        }

        return null;
    }

    private TrafficSourceDetails chocolateTrafficSource(int mpxChnlId, int chnl, String url, long rotId) {
        TrafficSourceDetails details = new TrafficSourceDetails();
        if (mpxChnlId == 25) {
            details.setTrafficSourceLevel3(ORGANIC_NAV_SEARCH_PAID);
        } else if (mpxChnlId == 2 && StringUtils.isNotEmpty(url)) {
            String keyword = UrlUtils.decode(UrlUtils.getParamValue(url, KEYWORD_PARAM)).trim().toLowerCase();
            if (EBAY_PATTERN.matcher(keyword).find()) {
                details.setTrafficSourceLevel3(ORGANIC_NAV_SEARCH_PAID);
            }
        } else if (mpxChnlId == 2) {
            details.setTrafficSourceLevel3(PAID_PAID_SEARCH);
        } else if (mpxChnlId == 6) {
            details.setTrafficSourceLevel3(PAID_EPN);
        } else if (mpxChnlId == 36) {
            details.setTrafficSourceLevel3(FREE_SEO_FREE_FEEDS);
        } else if (mpxChnlId == 1) {
            details.setTrafficSourceLevel3(PAID_DISPLAY);
        } else if (mpxChnlId == 33 || mpxChnlId == 35) {
            details.setTrafficSourceLevel3(PAID_PAID_SOCIAL);
        } else if (chnl == 7) {
            details.setTrafficSourceLevel3(ORGANIC_TXN_COMMS_SITE_EMAIL);
        } else if (chnl == 8) {
            details.setTrafficSourceLevel3(FREE_MKTG_COMMS_MKTG_EMAIL);
        } else if (chnl == 24) {
            details.setTrafficSourceLevel3(FREE_MKTG_COMMS_SMS);
        } else if (chnl == 29) {
            details.setTrafficSourceLevel3(ORGANIC_TXN_COMMS_CS_EMAIL);
        } else if (chnl == 16) {
            details.setTrafficSourceLevel3(FREE_FREE_SOCIAL);
        }
        // fallback with chnl
        else if (chnl == 1) {
            details.setTrafficSourceLevel3(PAID_EPN);
        } else if (chnl == 2) {
            details.setTrafficSourceLevel3(PAID_PAID_SEARCH);
        } else if (chnl == 4) {
            details.setTrafficSourceLevel3(PAID_DISPLAY);
        } else if (chnl == 28) {
            details.setTrafficSourceLevel3(FREE_SEO_FREE_FEEDS);
        }
        if (StringUtils.isNotEmpty(details.getTrafficSourceLevel3())) {
            details.setRotid(rotId);
            details.setMpxChnlId(mpxChnlId);
        }

        return details;
    }

    private boolean containsKey(String str, List<String> keywords) {
        return keywords.stream().anyMatch(str::contains);
    }

}
