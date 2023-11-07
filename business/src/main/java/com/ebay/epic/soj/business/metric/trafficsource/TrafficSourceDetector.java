package com.ebay.epic.soj.business.metric.trafficsource;

import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.*;
import com.ebay.epic.soj.common.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.TestOnly;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ebay.epic.soj.common.enums.EventType.*;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.*;

@Slf4j
public class TrafficSourceDetector {
    private final TrafficSourceLookupManager lookupManager;

    public TrafficSourceDetector() {
        lookupManager = TrafficSourceLookupManager.getInstance();
    }

    @TestOnly
    public TrafficSourceDetector(TrafficSourceLookupManager lookupManager) {
        this.lookupManager = lookupManager;
    }

    private final List<String> MNGD_TRAFFIC_SOURCE_LEVEL1_MNGD_FREE = Arrays.asList(FREE_FREE_SOCIAL,
            FREE_MKTG_COMMS_MKTG_EMAIL, FREE_MKTG_COMMS_SMS, FREE_MRKT_COMMS_NOTIF, ORGANIC_TXN_COMMS_CS_EMAIL,
            ORGANIC_TXN_COMMS_SITE_EMAIL, ORGANIC_TXN_COMMS_WEBMAIL, ORGANIC_TXN_COMMS_NOTIF);
    private final List<String> MNGD_TRAFFIC_SOURCE_LEVEL1_MNGD_PAID = Arrays.asList(PAID_DISPLAY, PAID_EPN,
            PAID_PAID_SEARCH, PAID_PAID_SOCIAL);
    private final List<String> MNGD_TRAFFIC_SOURCE_LEVEL1_SEO = Arrays.asList(FREE_SEO_FREE_FEEDS, FREE_SEO_NATURAL_SEARCH);
    private final List<String> MNGD_TRAFFIC_SOURCE_LEVEL1_UNMNGD = Arrays.asList(FREE_OTHER, ORGANIC_DIRECT_NO_REF,
            ORGANIC_DIRECT_ON_EBAY, ORGANIC_IMBD, ORGANIC_NAV_SEARCH_FREE);

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
                        try {
                            event.setChnl(Integer.parseInt(payload.get(PAYLOAD_KEY_CHNL)));
                        } catch (NumberFormatException e) {
                            log.warn("Cannot parse chnl tag", e);
                        }
                    }
                    if (StringUtils.isNotEmpty(payload.get(PAYLOAD_KEY_ROTID))) {
                        Long rotid = null;
                        try {
                            rotid = Long.parseLong(payload.get(PAYLOAD_KEY_ROTID));
                        } catch (NumberFormatException e) {
                            log.warn("Cannot parse rotid tag", e);
                        }
                        if (rotid != null) {
                            event.setRotId(rotid);
                            if (lookupManager.getDwMpxRotationMap().get(rotid) != null) {
                                event.setMpxChnlId(lookupManager.getDwMpxRotationMap().get(rotid).getMpxChnlId());
                            }
                        }
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
                event.setNtype(uniEvent.getPayload().get(PAYLOAD_KEY_NTYPE));
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
            else if (lookupManager.getPageMap().get(uniEvent.getPageId()) != null
                    && lookupManager.getPageMap().get(uniEvent.getPageId()).getIframe() == 0
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
                && lookupManager.getPageMap().get(uniEvent.getPageId()) != null
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
        // traffic source level 3
        TrafficSourceDetails details = detectTrafficSourceLevel3(trafficSourceCandidates);

        // traffic source level 1 and 2
        if (details != null && StringUtils.isNotEmpty(details.getTrafficSourceLevel3())) {
            String trafficSourceLevel3 = details.getTrafficSourceLevel3();
            details.setMngdTrafficSourceLevel1(detectMngdTrafficSourceLevel1(trafficSourceLevel3));
            details.setCustTrafficSourceLevel1(detectCustTrafficSourceLevel1(trafficSourceLevel3));
            details.setCustTrafficSourceLevel2(detectCustTrafficSourceLevel2(trafficSourceLevel3));
        }

        return details;
    }

    public TrafficSourceDetails detectTrafficSourceLevel3(TrafficSourceCandidates trafficSourceCandidates) {
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
                if (utpEvent.getNtype() != null && utpEvent.getNtype().contains(MRKT_NOTIF_NTYPE)) {
                    details.setTrafficSourceLevel3(FREE_MRKT_COMMS_NOTIF);
                    details.setNtype(utpEvent.getNtype());
                } else {
                    details.setTrafficSourceLevel3(ORGANIC_TXN_COMMS_NOTIF);
                    details.setNtype(utpEvent.getNtype());
                }
                return details;
            }
        }

        // fallback Chocolate traffic source using first valid event
        String mkcid = UrlUtils.getParamValue(firstEventUrl, "mkcid");
        if (StringUtils.isNotEmpty(mkcid) && CHOCOLATE_CHNL.contains(mkcid)) {
            Long rotId = null;
            Integer mpxChnlId = null;
            Integer chnl = null;
            try {
                chnl = Integer.parseInt(mkcid);
            } catch (NumberFormatException e) {
                log.warn("Cannot parse mkcid", e);
            }
            String mkrid = UrlUtils.getParamValue(firstEventUrl, "mkrid");
            if (StringUtils.isNotEmpty(mkrid)) {
                String mkridNum = UrlUtils.decode(mkrid).replaceAll("[-\\n\\r\\t\\s]", "");
                try {
                    rotId = Long.parseLong(mkridNum);
                } catch (NumberFormatException e) {
                    log.warn("Cannot parse rotation id", e);
                }
                if (rotId != null && lookupManager.getDwMpxRotationMap().get(rotId) != null) {
                    mpxChnlId = lookupManager.getDwMpxRotationMap().get(rotId).getMpxChnlId();
                }
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
        } else if (Integer.valueOf(15).equals(mpxChnlId) || Integer.valueOf(23).equals(mpxChnlId)) {
            details.setTrafficSourceLevel3(ORGANIC_IMBD);
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

    private String detectMngdTrafficSourceLevel1(String trafficSourceLevel3) {
        if (MNGD_TRAFFIC_SOURCE_LEVEL1_MNGD_FREE.contains(trafficSourceLevel3)) {
            return LEVEL1_MANAGED_FREE;
        } else if (ORGANIC_NAV_SEARCH_PAID.equals(trafficSourceLevel3)) {
            return LEVEL1_MANAGED_NAV_SEARCH;
        } else if (MNGD_TRAFFIC_SOURCE_LEVEL1_MNGD_PAID.contains(trafficSourceLevel3)) {
            return LEVEL1_MANAGED_PAID;
        } else if (MNGD_TRAFFIC_SOURCE_LEVEL1_SEO.contains(trafficSourceLevel3)) {
            return LEVEL1_SEO;
        } else if (MNGD_TRAFFIC_SOURCE_LEVEL1_UNMNGD.contains(trafficSourceLevel3)) {
            return LEVEL1_UNMANAGED;
        }

        return TRAFFIC_SOUCE_OTHER;
    }

    private String detectCustTrafficSourceLevel1(String trafficSourceLevel3) {
        if (trafficSourceLevel3.startsWith(LEVEL1_FREE)) {
            return LEVEL1_FREE;
        } else if (trafficSourceLevel3.startsWith(LEVEL1_ORGANIC)) {
            return LEVEL1_ORGANIC;
        } else if (trafficSourceLevel3.startsWith(LEVEL1_PAID)) {
            return LEVEL1_PAID;
        }

        return TRAFFIC_SOUCE_OTHER;
    }

    private String detectCustTrafficSourceLevel2(String trafficSourceLevel3) {
        if (FREE_FREE_SOCIAL.equals(trafficSourceLevel3)) {
            return LEVEL2_FREE_FREE_SOCIAL;
        } else if (trafficSourceLevel3.startsWith(LEVEL2_FREE_MKTG_COMMS)) {
            return LEVEL2_FREE_MKTG_COMMS;
        } else if (FREE_OTHER.equals(trafficSourceLevel3)) {
            return LEVEL2_FREE_OTHER;
        } else if (trafficSourceLevel3.startsWith(LEVEL2_FREE_SEO)) {
            return LEVEL2_FREE_SEO;
        } else if (trafficSourceLevel3.startsWith(LEVEL2_ORGANIC_DIRECT)) {
            return LEVEL2_ORGANIC_DIRECT;
        } else if (ORGANIC_IMBD.equals(trafficSourceLevel3)) {
            return LEVEL2_ORGANIC_IMBD;
        } else if (ORGANIC_NAV_SEARCH_FREE.equals(trafficSourceLevel3)) {
            return LEVEL2_ORGANIC_NAV_SEARCH_FREE;
        } else if (ORGANIC_NAV_SEARCH_PAID.equals(trafficSourceLevel3)) {
            return LEVEL2_ORGANIC_NAV_SEARCH_PAID;
        } else if (trafficSourceLevel3.startsWith(LEVEL2_ORGANIC_TXN_COMMS)) {
            return LEVEL2_ORGANIC_TXN_COMMS;
        } else if (PAID_DISPLAY.equals(trafficSourceLevel3)) {
            return LEVEL2_PAID_DISPLAY;
        } else if (PAID_EPN.equals(trafficSourceLevel3)) {
            return LEVEL2_PAID_EPN;
        } else if (PAID_PAID_SEARCH.equals(trafficSourceLevel3)) {
            return LEVEL2_PAID_PAID_SEARCH;
        } else if (PAID_PAID_SOCIAL.equals(trafficSourceLevel3)) {
            return LEVEL2_PAID_PAID_SOCIAL;
        }

        return TRAFFIC_SOUCE_OTHER;
    }

    private boolean containsKey(String str, List<String> keywords) {
        if (StringUtils.isNotEmpty(str)) {
            return keywords.stream().anyMatch(str::contains);
        } else {
            return false;
        }
    }

}
