package com.ebay.epic.soj.business.metric.trafficsource;

import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.*;
import com.ebay.epic.soj.common.utils.UrlUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.ebay.epic.soj.business.testUtil.ModelFactoryUtils.*;
import static com.ebay.epic.soj.common.enums.EventType.*;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidateType.*;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrafficSourceDetectorTest {
    private static TrafficSourceDetector detector;

    private static final int HOME_PAGE = 2481888;
    private static final String HOME_PAGE_NAME = "Home Page";
    private static final int VI_PAGE = 2349624;
    private static final String VI_PAGE_NAME = "ViewItem";
    private static final int IFRAME_PAGE = 2356359;
    private static final String IFRAME_PAGE_NAME = "Pulsar Gateway";

    private static final long TEST_RID = 7101597872055385L;
    private static final long IMBD_RID = 71186042134093L;

    private static final String GOOGLE_REF = "https://www.google.com";
    private static final String FACEBOOK_REF = "https://www.facebook.com";
    private static final String MAIL_REF = "https://mail.google.com";
    private static final String OTHER_REF = "https://testUrl.com";

    private static final String HOME_PAGE_URL = "https://www.ebay.com";
    private static final String TEST_URL = "https://www.ebay.de/itm/324693944214?mkevt=1&mkcid=1&mkrid=707-53477-19255-0";
    private static final String PAID_SEARCH_KEYWORD_URL = "https://www.ebay.co.uk/sl/sell?norover=1&mkevt=1" +
            "&mkrid=710-159787-205538-5&mkcid=2&keyword=sell%20on%20ebay";
    private static final String PAID_SEARCH_MISSPELLING_URL = "https://www.ebay.co.uk/sl/sell?norover=1&mkevt=1" +
            "&mkrid=710-159787-205538-5&mkcid=2&keyword=E+BAY";
    private static final String PAID_SEARCH_SUBSIDIARY_URL = "https://www.ebay.co.uk/sl/sell?norover=1&mkevt=1" +
            "&mkrid=710-159787-205538-5&mkcid=2&keyword=kijiji";
    private static final String PAID_SEARCH_NON_EBAY_URL = "https://www.ebay.co.uk/sl/sell?norover=1&mkevt=1" +
            "&mkrid=710-159787-205538-5&mkcid=2&keyword=iphone";

    private static final long FIRST_EVENT_TS = System.currentTimeMillis();
    private static final long EVENT_TS_ADD_3_SEC = FIRST_EVENT_TS + 3 * 1000;
    private static final long EVENT_TS_ADD_10_SEC = FIRST_EVENT_TS + 10 * 1000;
    private static final long EVENT_TS_ADD_20_MIN = FIRST_EVENT_TS + 20 * 60 * 1000;

    private static final String MRKT_NTYPE = "AdhocMarketingCampaign_Adhoc";
    private static final String SITE_NTYPE = "ShipmentUpdate_LateDelivery";

    @BeforeClass
    public static void init() {
        TrafficSourceLookupManager lookupManager = mock(TrafficSourceLookupManager.class);
        mockPageLookup(lookupManager);
        mockRotationLookup(lookupManager);
        detector = new TrafficSourceDetector(lookupManager);
    }

    @Test
    public void extractCandidate_ubi_valid() {
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, HOME_PAGE, 0, HOME_PAGE_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UBI);
        ValidUbiEvent event = (ValidUbiEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(HOME_PAGE);
        assertThat(event.getPageName()).isEqualTo(HOME_PAGE_NAME);
        assertThat(event.getUrl()).isEqualTo(HOME_PAGE_URL);
        assertThat(event.getReferer()).isEqualTo(GOOGLE_REF);
    }

    @Test
    public void extractCandidate_ubi_payloadReferer() {
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, HOME_PAGE, 0, HOME_PAGE_URL, "");
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_REF, FACEBOOK_REF);
        uniEvent.setPayload(payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UBI);
        ValidUbiEvent event = (ValidUbiEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(HOME_PAGE);
        assertThat(event.getPageName()).isEqualTo(HOME_PAGE_NAME);
        assertThat(event.getUrl()).isEqualTo(HOME_PAGE_URL);
        assertThat(event.getReferer()).isEqualTo(FACEBOOK_REF);
    }

    @Test
    public void extractCandidate_ubiIframe_invalid() {
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, IFRAME_PAGE, 0, HOME_PAGE_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate).isNull();
    }

    @Test
    public void extractCandidate_ubiRdt_invalid() {
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, HOME_PAGE, 1, HOME_PAGE_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate).isNull();
    }

    @Test
    public void extractCandidate_surfaceWeb_valid() {
        UniEvent uniEvent = getUniEvent(AUTOTRACK_WEB, HOME_PAGE, 0, HOME_PAGE_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(SURFACE);
        ValidSurfaceEvent event = (ValidSurfaceEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(HOME_PAGE);
        assertThat(event.getPageName()).isEqualTo(HOME_PAGE_NAME);
        assertThat(event.getUrl()).isEqualTo(HOME_PAGE_URL);
        assertThat(event.getReferer()).isEqualTo(GOOGLE_REF);
    }

    @Test
    public void extractCandidate_surfaceNative_invalid() {
        UniEvent uniEvent = getUniEvent(AUTOTRACK_NATIVE, HOME_PAGE, 0, HOME_PAGE_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(SURFACE);
        ValidSurfaceEvent event = (ValidSurfaceEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(HOME_PAGE);
        assertThat(event.getPageName()).isEqualTo(HOME_PAGE_NAME);
        assertThat(event.getUrl()).isEqualTo(HOME_PAGE_URL);
        assertThat(event.getReferer()).isEqualTo(GOOGLE_REF);
    }

    @Test
    public void extractCandidate_chocolate() {
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_CHNL, "1");
        payload.put(PAYLOAD_KEY_ROTID, String.valueOf(TEST_RID));
        payload.put(PAYLOAD_KEY_URL_MPRE, UrlUtils.encode(TEST_URL));
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, CHOCOLATE_PAGE, 0, GOOGLE_REF, payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UTP);
        UtpEvent event = (UtpEvent) candidate;
        assertThat(event.getChnl()).isEqualTo(1);
        assertThat(event.getRotId()).isEqualTo(TEST_RID);
        assertThat(event.getMpxChnlId()).isEqualTo(6);
        assertThat(event.getPageId()).isEqualTo(CHOCOLATE_PAGE);
        assertThat(event.getUrl()).isEqualTo(TEST_URL);
    }

    @Test
    public void extractCandidate_chocolate_nullFields() {
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_CHNL, "7");
        payload.put(PAYLOAD_KEY_URL_MPRE, UrlUtils.encode(TEST_URL));
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, CHOCOLATE_PAGE, 0, GOOGLE_REF, payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UTP);
        UtpEvent event = (UtpEvent) candidate;
        assertThat(event.getChnl()).isEqualTo(7);
        assertThat(event.getRotId()).isNull();
        assertThat(event.getMpxChnlId()).isNull();
        assertThat(event.getPageId()).isEqualTo(CHOCOLATE_PAGE);
        assertThat(event.getUrl()).isEqualTo(TEST_URL);
    }

    @Test
    public void extractCandidate_notification() {
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_PNACT, "1");
        payload.put(PAYLOAD_KEY_NTYPE, MRKT_NTYPE);
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, NOTIFICATION_PAGE, 0, GOOGLE_REF, payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UTP);
        UtpEvent event = (UtpEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(NOTIFICATION_PAGE);
        assertThat(event.getNtype()).isEqualTo(MRKT_NTYPE);
        assertThat(event.getUrl()).isNull();
    }

    @Test
    public void extractCandidate_imbd() {
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_MPPID, "1");
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, IMBD_PAGE, 0, GOOGLE_REF, payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(IMBD);
        ImbdEvent event = (ImbdEvent) candidate;
        assertThat(event.getMppid()).isEqualTo("1");
    }

    @Test
    public void extractCandidate_deeplink() {
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_REF, UrlUtils.encode(GOOGLE_REF));
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, DEEPLINK_PAGE, 0, "", payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(DEEPLINK);
        DeeplinkActionEvent event = (DeeplinkActionEvent) candidate;
        assertThat(event.getReferer()).isEqualTo(GOOGLE_REF);
    }

    @Test
    public void determineTrafficSource_noValidEvent() {
        ImbdEvent imbdEvent = getImbdEvent(FIRST_EVENT_TS, RandomStringUtils.random(5));
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder().firstImbdEvent(imbdEvent).build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);

        assertThat(details).isNull();
    }

    @Test
    public void determineTrafficSource_firstEvent() {
        // Only UBI event
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder().firstValidUbiEvent(ubiEvent).build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));

        // Only Surface event
        ValidSurfaceEvent surfaceEvent =  getValidSurfaceEvent(EVENT_TS_ADD_3_SEC, VI_PAGE, VI_PAGE_NAME, HOME_PAGE_URL, FACEBOOK_REF);
        candidates = TrafficSourceCandidates.builder().firstValidSurfaceEvent(surfaceEvent).build();
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getLdngPageId()).isEqualTo(VI_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(FACEBOOK_REF));

        // UBI early
        candidates.setFirstValidUbiEvent(ubiEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));

        // Surface early
        ubiEvent.setEventTimestamp(EVENT_TS_ADD_10_SEC);
        candidates.setFirstValidUbiEvent(ubiEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getLdngPageId()).isEqualTo(VI_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(FACEBOOK_REF));
    }

    @Test
    public void determineTrafficSource_fallbackReferer() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, null);
        DeeplinkActionEvent deeplinkActionEvent = getDeeplinkActionEvent(EVENT_TS_ADD_3_SEC, GOOGLE_REF);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstDeeplinkActionEvent(deeplinkActionEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
    }

    @Test
    public void determineTrafficSource_organicNavSearchPaid() {
        // Paid Search Brand
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 2, TEST_RID, 25, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_NAV_SEARCH);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(25);

        // Paid Search with ebay keyword
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 2, TEST_RID, 2, PAID_SEARCH_KEYWORD_URL);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_NAV_SEARCH);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(2);

        // Paid Search with ebay misspelling keyword
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 2, TEST_RID, 2, PAID_SEARCH_MISSPELLING_URL);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_NAV_SEARCH);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(2);

        // Paid Search with ebay subsidiary keyword
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 2, TEST_RID, 2, PAID_SEARCH_SUBSIDIARY_URL);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_NAV_SEARCH);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_NAV_SEARCH_PAID);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(2);
    }

    @Test
    public void determineTrafficSource_paidPaidSearch() {
        // Paid Search without keyword
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 2, TEST_RID, 2, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_PAID_SEARCH);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_PAID_SEARCH);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(2);

        // Paid Search with non-ebay keyword
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 2, TEST_RID, 2, PAID_SEARCH_NON_EBAY_URL);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_PAID_SEARCH);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_PAID_SEARCH);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(2);

        // fallback by chnl
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 2, null, null, null);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_PAID_SEARCH);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_PAID_SEARCH);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_paidEpn() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 1, TEST_RID, 6, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_EPN);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_EPN);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(6);

        // fallback by chnl
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 1, null, null, null);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_EPN);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_EPN);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_freeSeoFreeFeeds() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 28, TEST_RID, 36, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_SEO);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_SEO);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_SEO_FREE_FEEDS);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(36);

        // fallback by chnl
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 28, null, null, null);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_SEO);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_SEO);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_SEO_FREE_FEEDS);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_paidDisplay() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 4, TEST_RID, 1, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_DISPLAY);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_DISPLAY);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(1);

        // fallback by chnl
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 4, null, null, null);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_DISPLAY);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_DISPLAY);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_paidPaidSocial() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 16, TEST_RID, 33, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_PAID_SOCIAL);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_PAID_SOCIAL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(33);

        // fallback by chnl
        utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 16, TEST_RID, 35, HOME_PAGE_URL);
        candidates.setFirstUtpEvent(utpEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_PAID_SOCIAL);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_PAID_SOCIAL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(35);
    }

    @Test
    public void determineTrafficSource_organicSiteEmail() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 7, null, null, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_TXN_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_TXN_COMMS_SITE_EMAIL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_freeMrktEmail() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 8, null, null, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_MKTG_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_MKTG_COMMS_MKTG_EMAIL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_freeMrktSms() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 24, null, null, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_MKTG_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_MKTG_COMMS_SMS);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_organicGcxEmail() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 29, null, null, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_TXN_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_TXN_COMMS_CS_EMAIL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_freeMrktCommsNotif() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpNotifEvent(EVENT_TS_ADD_3_SEC, NOTIFICATION_PAGE, MRKT_NTYPE);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_MKTG_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_MRKT_COMMS_NOTIF);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getNtype()).isEqualTo(MRKT_NTYPE);
        assertThat(details.getRotid()).isEqualTo(0);
        assertThat(details.getMpxChnlId()).isEqualTo(0);
    }

    @Test
    public void determineTrafficSource_organicTxnCommsNotif() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpNotifEvent(EVENT_TS_ADD_3_SEC, NOTIFICATION_PAGE, SITE_NTYPE);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_TXN_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_TXN_COMMS_NOTIF);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getNtype()).isEqualTo(SITE_NTYPE);
        assertThat(details.getRotid()).isEqualTo(0);
        assertThat(details.getMpxChnlId()).isEqualTo(0);
    }

    @Test
    public void determineTrafficSource_fallbackChocolate() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, PAID_SEARCH_KEYWORD_URL, GOOGLE_REF);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_PAID);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_PAID);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_PAID_EPN);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(PAID_EPN);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isEqualTo(TEST_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(6);
    }

    @Test
    public void determineTrafficSource_imbd() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        String mppid = String.valueOf(new Random().nextInt(10000));
        ImbdEvent imbdEvent = getImbdEvent(EVENT_TS_ADD_3_SEC, mppid);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstImbdEvent(imbdEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_IMBD);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_IMBD);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getMppid()).isEqualTo(mppid);
        assertThat(details.getRotid()).isEqualTo(0);
        assertThat(details.getMpxChnlId()).isEqualTo(0);
    }

    @Test
    public void determineTrafficSource_imbd_chocolate() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 4, IMBD_RID, 15, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_IMBD);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_IMBD);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getMppid()).isNull();
        assertThat(details.getRotid()).isEqualTo(IMBD_RID);
        assertThat(details.getMpxChnlId()).isEqualTo(15);
    }

    @Test
    public void determineTrafficSource_freeFreeSocial_chocolate() {
        // Chocolate
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 16, null, null, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_FREE_SOCIAL);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_FREE_SOCIAL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
        assertThat(details.getRotid()).isNull();
        assertThat(details.getMpxChnlId()).isNull();
    }

    @Test
    public void determineTrafficSource_freeSeoNaturalSearch() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, VI_PAGE, VI_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_10_SEC, CHOCOLATE_PAGE, 7, null, null, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_SEO);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_SEO);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_SEO_NATURAL_SEARCH);
        assertThat(details.getLdngPageId()).isEqualTo(VI_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
    }

    @Test
    public void determineTrafficSource_organicNavSearchFree() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_10_SEC, CHOCOLATE_PAGE, 7, null, null, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstUtpEvent(utpEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_NAV_SEARCH_FREE);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_NAV_SEARCH_FREE);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(GOOGLE_REF));
    }

    @Test
    public void determineTrafficSource_organicDirectOnEbay() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, HOME_PAGE_URL);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_DIRECT);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_DIRECT_ON_EBAY);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(HOME_PAGE_URL));
    }

    @Test
    public void determineTrafficSource_freeFreeSocial_referer() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, FACEBOOK_REF);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_FREE_SOCIAL);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_FREE_SOCIAL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(FACEBOOK_REF));
    }

    @Test
    public void determineTrafficSource_organicWebmail() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, MAIL_REF);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_TXN_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_TXN_COMMS_WEBMAIL);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(MAIL_REF));
    }

    @Test
    public void determineTrafficSource_organicDirectNoRef() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, null);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_DIRECT);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_DIRECT_NO_REF);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isNull();
        assertThat(details.getRotid()).isEqualTo(0);

        ubiEvent.setReferer("null");
        candidates.setFirstValidUbiEvent(ubiEvent);
        details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_DIRECT);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_DIRECT_NO_REF);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isNull();
    }

    @Test
    public void determineTrafficSource_freeOther() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(FIRST_EVENT_TS, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, OTHER_REF);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_OTHER);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_OTHER);
        assertThat(details.getLdngPageId()).isEqualTo(HOME_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(OTHER_REF));
    }

    @Test
    public void determineTrafficSource_complexSession_chocolate() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(EVENT_TS_ADD_3_SEC, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        ValidSurfaceEvent surfaceEvent = getValidSurfaceEvent(FIRST_EVENT_TS, VI_PAGE, VI_PAGE_NAME, HOME_PAGE_URL, FACEBOOK_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_3_SEC, CHOCOLATE_PAGE, 7, null, null, TEST_URL);
        ImbdEvent imbdEvent = getImbdEvent(EVENT_TS_ADD_20_MIN, RandomStringUtils.random(5));
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstValidSurfaceEvent(surfaceEvent)
                .firstUtpEvent(utpEvent)
                .firstImbdEvent(imbdEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_TXN_COMMS);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_TXN_COMMS_SITE_EMAIL);
        assertThat(details.getLdngPageId()).isEqualTo(VI_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(FACEBOOK_REF));
        assertThat(details.getRotid()).isNull();
    }

    @Test
    public void determineTrafficSource_complexSession_imbd() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(EVENT_TS_ADD_3_SEC, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        ValidSurfaceEvent surfaceEvent = getValidSurfaceEvent(FIRST_EVENT_TS, VI_PAGE, VI_PAGE_NAME, HOME_PAGE_URL, FACEBOOK_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_10_SEC, NOTIFICATION_PAGE, null, null, null, null);
        String mppid = String.valueOf(new Random().nextInt(10000));
        ImbdEvent imbdEvent = getImbdEvent(EVENT_TS_ADD_10_SEC, mppid);
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstValidSurfaceEvent(surfaceEvent)
                .firstUtpEvent(utpEvent)
                .firstImbdEvent(imbdEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_UNMANAGED);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_ORGANIC);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_ORGANIC_IMBD);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(ORGANIC_IMBD);
        assertThat(details.getLdngPageId()).isEqualTo(VI_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(FACEBOOK_REF));
        assertThat(details.getMppid()).isEqualTo(mppid);
    }

    @Test
    public void determineTrafficSource_complexSession_referer() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(EVENT_TS_ADD_3_SEC, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        ValidSurfaceEvent surfaceEvent = getValidSurfaceEvent(FIRST_EVENT_TS, VI_PAGE, VI_PAGE_NAME, HOME_PAGE_URL, FACEBOOK_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_10_SEC, NOTIFICATION_PAGE, null, null, null, null);
        ImbdEvent imbdEvent = getImbdEvent(EVENT_TS_ADD_20_MIN, RandomStringUtils.random(5));
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstValidSurfaceEvent(surfaceEvent)
                .firstUtpEvent(utpEvent)
                .firstImbdEvent(imbdEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_FREE_SOCIAL);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_FREE_SOCIAL);
        assertThat(details.getLdngPageId()).isEqualTo(VI_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(FACEBOOK_REF));
    }

    @Test
    public void determineTrafficSource_complexSession_deeplinkReferer() {
        ValidUbiEvent ubiEvent = getValidUbiEvent(EVENT_TS_ADD_3_SEC, HOME_PAGE, HOME_PAGE_NAME, HOME_PAGE_URL, GOOGLE_REF);
        ValidSurfaceEvent surfaceEvent = getValidSurfaceEvent(FIRST_EVENT_TS, VI_PAGE, VI_PAGE_NAME, HOME_PAGE_URL, null);
        DeeplinkActionEvent deeplinkActionEvent = getDeeplinkActionEvent(EVENT_TS_ADD_3_SEC, FACEBOOK_REF);
        UtpEvent utpEvent = getUtpEvent(EVENT_TS_ADD_10_SEC, NOTIFICATION_PAGE, null, null, null, null);
        ImbdEvent imbdEvent = getImbdEvent(EVENT_TS_ADD_20_MIN, RandomStringUtils.random(5));
        TrafficSourceCandidates candidates = TrafficSourceCandidates.builder()
                .firstValidUbiEvent(ubiEvent)
                .firstValidSurfaceEvent(surfaceEvent)
                .firstDeeplinkActionEvent(deeplinkActionEvent)
                .firstUtpEvent(utpEvent)
                .firstImbdEvent(imbdEvent)
                .build();
        TrafficSourceDetails details = detector.determineTrafficSource(candidates);
        assertThat(details.getMngdTrafficSourceLevel1()).isEqualTo(LEVEL1_MANAGED_FREE);
        assertThat(details.getCustTrafficSourceLevel1()).isEqualTo(LEVEL1_FREE);
        assertThat(details.getCustTrafficSourceLevel2()).isEqualTo(LEVEL2_FREE_FREE_SOCIAL);
        assertThat(details.getTrafficSourceLevel3()).isEqualTo(FREE_FREE_SOCIAL);
        assertThat(details.getLdngPageId()).isEqualTo(VI_PAGE);
        assertThat(details.getReferer()).isEqualTo(UrlUtils.getDomain(FACEBOOK_REF));
    }

    private static void mockPageLookup(TrafficSourceLookupManager lookupManager) {
        Map<Integer, Page > pageMap = new HashMap<>();
        pageMap.put(HOME_PAGE, getPage(HOME_PAGE, HOME_PAGE_NAME, 0));
        pageMap.put(CHOCOLATE_PAGE, getPage(CHOCOLATE_PAGE, "mktcollectionsvc__DefaultPage", 1));
        pageMap.put(IFRAME_PAGE, getPage(IFRAME_PAGE, IFRAME_PAGE_NAME, 1));
        when(lookupManager.getPageMap()).thenReturn(pageMap);
    }

    private static void mockRotationLookup(TrafficSourceLookupManager lookupManager) {
        Map<Long, DwMpxRotation> rotationMap = new HashMap<>();
        rotationMap.put(TEST_RID, getMpxRotation(TEST_RID, 6));
        rotationMap.put(IMBD_RID, getMpxRotation(IMBD_RID, 15));
        when(lookupManager.getDwMpxRotationMap()).thenReturn(rotationMap);
    }
}
