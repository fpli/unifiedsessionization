package com.ebay.epic.soj.business.metric.trafficsource;

import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.*;
import com.ebay.epic.soj.common.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Map;

import static com.ebay.epic.soj.common.enums.EventType.*;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.*;

public class TrafficSourceDetector {
    private final TrafficSourceLookupManager lookupManager;

    public TrafficSourceDetector() {
        lookupManager = TrafficSourceLookupManager.getInstance();
    }

    @TestOnly
    public TrafficSourceDetector(TrafficSourceLookupManager lookupManager) {
        this.lookupManager = lookupManager;
    }

    public TrafficSourceCandidate extractCandidate(UniEvent uniEvent) {
        int pageId = uniEvent.getPageId();
        // UBI event
        if (UBI_BOT.equals(uniEvent.getEventType()) || UBI_NONBOT.equals(uniEvent.getEventType())) {
            // Chocolate event
            if (pageId == CHOCOLATE_PAGE) {
                UtpEvent event = new UtpEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                if (uniEvent.getPayload() != null) {
                    Map<String,String> payload = uniEvent.getPayload();
                    if (StringUtils.isNotEmpty(payload.get(PAYLOAD_KEY_CHNL))) {
                        event.setChnl(Integer.parseInt(payload.get(PAYLOAD_KEY_CHNL)));
                    }
                    if (StringUtils.isNotEmpty(payload.get(PAYLOAD_KEY_ROTID))) {
                        long rotid = Long.parseLong(payload.get(PAYLOAD_KEY_ROTID));
                        event.setRotId(rotid);
                        event.setMpxChnlId(lookupManager.getDwMpxRotationMap().get(rotid).getMpxChnlId());
                    }
                    event.setUrl(UrlUtils.decode(payload.get(PAYLOAD_KEY_URL_MPRE)));
                }
                event.setPageId(pageId);
                return event;
            }
            // Notification event
            else if (pageId == NOTIFICATION_PAGE && uniEvent.getPayload() != null
                    && "1".equals(uniEvent.getPayload().get(PAYLOAD_KEY_PNACT))) {
                UtpEvent event = new UtpEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setPageId(pageId);
                return event;
            }
            // IMBD event
            else if (pageId == IMBD_PAGE && uniEvent.getPayload() != null
                    && StringUtils.isNotEmpty(uniEvent.getPayload().get(PAYLOAD_KEY_MPPID))) {
                ImbdEvent event = new ImbdEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setMppid(uniEvent.getPayload().get(PAYLOAD_KEY_MPPID));
                return event;
            }
            // Deeplink event
            else if (pageId == DEEPLINK_PAGE && uniEvent.getPayload() != null
                    && StringUtils.isNotEmpty(uniEvent.getPayload().get(PAYLOAD_KEY_REF))
                    && !UNKNOWN_REF.equals(uniEvent.getPayload().get(PAYLOAD_KEY_REF))) {
                DeeplinkActionEvent event = new DeeplinkActionEvent();
                event.setEventTimestamp(uniEvent.getEventTs());
                event.setReferer(UrlUtils.decode(uniEvent.getPayload().get(PAYLOAD_KEY_REF)));
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
                if (StringUtils.isNotEmpty(uniEvent.getReferer())) {
                    event.setReferer(uniEvent.getReferer());
                } else if (uniEvent.getPayload() != null) {
                    event.setReferer(UrlUtils.decode(uniEvent.getPayload().get(PAYLOAD_KEY_REF)));
                }
                return event;
            }
        }

        // Surface valid event
        if ((AUTOTRACK_WEB.equals(uniEvent.getEventType()) || AUTOTRACK_NATIVE.equals(uniEvent.getEventType()))
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
        if (firstEventTs  == -1) {
            return null;
        }

        // fallback referer
        if (StringUtils.isEmpty(firstEventRef) && trafficSourceCandidates.getFirstDeeplinkActionEvent() != null) {
            firstEventRef = trafficSourceCandidates.getFirstDeeplinkActionEvent().getReferer();
        }

        // set common fields
        details.setLdngPageId(firstEventPageId);
        String refererDomain = UrlUtils.getDomain(firstEventRef);
        if (StringUtils.isNotEmpty(refererDomain)) {
            details.setReferer(refererDomain);
        }

        // UTP traffic source, including Chocolate and Notification
        // TODO: split Chocolate and Notification
        if (trafficSourceCandidates.getFirstUtpEvent() != null
                && Math.abs(trafficSourceCandidates.getFirstUtpEvent().getEventTimestamp() - firstEventTs)
                <= UTP_THRESHOLD) {
            UtpEvent utpEvent = trafficSourceCandidates.getFirstUtpEvent();
            Integer pageId = utpEvent.getPageId();

            // Chocolate
            Integer mpxChnlId = utpEvent.getMpxChnlId();
            Integer chnl = utpEvent.getChnl();
            String url = utpEvent.getUrl();
            detectChocolateTrafficSource(details, mpxChnlId, chnl, url, utpEvent.getRotId());
            if (StringUtils.isNotEmpty(details.getTrafficSourceLevel3())) {
                return details;
            }

            // Notification
            if (pageId == NOTIFICATION_PAGE) {
                details.setTrafficSourceLevel3(NOTIFICATIONS_APPS);
                return details;
            }
        }

        // fallback Chocolate traffic source using first valid event
        String mkcid = UrlUtils.getParamValue(firstEventUrl, "mkcid");
        if (StringUtils.isNotEmpty(mkcid) && CHOCOLATE_CHNL.contains(mkcid)) {
            Long rotId = null;
            Integer mpxChnlId = null;
            Integer chnl = Integer.parseInt(mkcid);
            String mkrid = UrlUtils.getParamValue(firstEventUrl, "mkrid");
            if (StringUtils.isNotEmpty(mkrid)) {
                String mkridNum = UrlUtils.decode(mkrid).replaceAll("-", "");
                rotId = Long.parseLong(mkridNum);
                mpxChnlId = lookupManager.getDwMpxRotationMap().get(rotId).getMpxChnlId();
            }
            detectChocolateTrafficSource(details, mpxChnlId, chnl, firstEventUrl, rotId);
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
        if (StringUtils.isNotEmpty(firstEventRef)) {
            if (containsKey(firstEventRef, SEARCH_ENGINE_KEYWORDS) && !firstEventRef.contains(MAIL_KEYWORD)) {
                if (!HOME_PAGE_NAME.equals(firstEventPageName)) {
                    details.setTrafficSourceLevel3(FREE_SEO_NATURAL_SEARCH);
                } else {
                    details.setTrafficSourceLevel3(ORGANIC_NAV_SEARCH_FREE);
                }
            } else if (firstEventRef.contains(EBAY_KEYWORD)) {
                details.setTrafficSourceLevel3(ORGANIC_DIRECT_ON_EBAY);
            } else if (containsKey(firstEventRef, SOCIAL_KEYWORDS)) {
                details.setTrafficSourceLevel3(FREE_FREE_SOCIAL);
            } else if (firstEventRef.contains(MAIL_KEYWORD)) {
                details.setTrafficSourceLevel3(ORGANIC_TXN_COMMS_WEBMAIL);
            } else if (NULL_KEYWORD.equals(firstEventRef)) {
                details.setTrafficSourceLevel3(ORGANIC_DIRECT_NO_REF);
            } else {
                details.setTrafficSourceLevel3(FREE_OTHER);
            }
        } else {
            details.setTrafficSourceLevel3(ORGANIC_DIRECT_NO_REF);
        }

        return details;
    }

    private void detectChocolateTrafficSource(TrafficSourceDetails details, Integer mpxChnlId, Integer chnl, String url, Long rotId) {
        if (Integer.valueOf(25).equals(mpxChnlId)) {
            details.setTrafficSourceLevel3(ORGANIC_NAV_SEARCH_PAID);
        } else if (Integer.valueOf(2).equals(mpxChnlId)) {
            String keywordParam = UrlUtils.getParamValue(url, KEYWORD_PARAM);
            if (StringUtils.isNotEmpty(keywordParam)) {
                String keyword = UrlUtils.decode(keywordParam).replaceAll(" ", "").toLowerCase();
                if (containsKey(keyword, EBAY_KEYWORDS)) {
                    details.setTrafficSourceLevel3(ORGANIC_NAV_SEARCH_PAID);
                } else {
                    details.setTrafficSourceLevel3(PAID_PAID_SEARCH);
                }
            } else {
                details.setTrafficSourceLevel3(PAID_PAID_SEARCH);
            }
        } else if (Integer.valueOf(6).equals(mpxChnlId)) {
            details.setTrafficSourceLevel3(PAID_EPN);
        } else if (Integer.valueOf(36).equals(mpxChnlId)) {
            details.setTrafficSourceLevel3(FREE_SEO_FREE_FEEDS);
        } else if (Integer.valueOf(1).equals(mpxChnlId)) {
            details.setTrafficSourceLevel3(PAID_DISPLAY);
        } else if (Integer.valueOf(33).equals(mpxChnlId) || Integer.valueOf(35).equals(mpxChnlId)) {
            details.setTrafficSourceLevel3(PAID_PAID_SOCIAL);
        } else if (Integer.valueOf(7).equals(chnl)) {
            details.setTrafficSourceLevel3(ORGANIC_TXN_COMMS_SITE_EMAIL);
        } else if (Integer.valueOf(8).equals(chnl)) {
            details.setTrafficSourceLevel3(FREE_MKTG_COMMS_MKTG_EMAIL);
        } else if (Integer.valueOf(24).equals(chnl)) {
            details.setTrafficSourceLevel3(FREE_MKTG_COMMS_SMS);
        } else if (Integer.valueOf(29).equals(chnl)) {
            details.setTrafficSourceLevel3(ORGANIC_TXN_COMMS_CS_EMAIL);
        } else if (Integer.valueOf(16).equals(chnl)) {
            details.setTrafficSourceLevel3(FREE_FREE_SOCIAL);
        }
        // fallback with chnl
        else if (Integer.valueOf(1).equals(chnl)) {
            details.setTrafficSourceLevel3(PAID_EPN);
        } else if (Integer.valueOf(2).equals(chnl)) {
            details.setTrafficSourceLevel3(PAID_PAID_SEARCH);
        } else if (Integer.valueOf(4).equals(chnl)) {
            details.setTrafficSourceLevel3(PAID_DISPLAY);
        } else if (Integer.valueOf(28).equals(chnl)) {
            details.setTrafficSourceLevel3(FREE_SEO_FREE_FEEDS);
        }
        if (StringUtils.isNotEmpty(details.getTrafficSourceLevel3())) {
            details.setRotid(rotId);
            details.setMpxChnlId(mpxChnlId);
        }
    }

    private boolean containsKey(String str, List<String> keywords) {
        if (StringUtils.isNotEmpty(str)) {
            return keywords.stream().anyMatch(str::contains);
        } else {
            return false;
        }
    }

}
