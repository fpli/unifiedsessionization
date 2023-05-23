package com.ebay.epic.soj.business.metric.trafficsource;

import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.*;
import com.ebay.epic.soj.common.utils.UrlUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
    private static final int IFRAME_PAGE = 2356359;
    private static final String IFRAME_PAGE_NAME = "Pulsar Gateway";

    private static final long EPN_RID = 70753477192550L;

    private static final String NORMAL_URL = "https://www.ebay.com";

    private static final String GOOGLE_REF = "https://www.google.com";

    private static final String EPN_URL = "https://www.ebay.de/itm/324693944214?mkevt=1&mkcid=1&mkrid=707-53477-19255-0";
    private static final String PAID_SEARCH_KEYWORD_URL = "https://www.ebay.com/sch/i.html?_nkw=hi+protein+dog+food" +
            "&mkevt=1&mkrid=21527-227144-52756-0&mkcid=2&keyword=ebay";

    @BeforeClass
    public static void init() {
        TrafficSourceLookupManager lookupManager = mock(TrafficSourceLookupManager.class);
        mockPageLookup(lookupManager);
        mockRotationLookup(lookupManager);
        detector = new TrafficSourceDetector(lookupManager);
    }

    @Test
    public void extractCandidate_ubi_valid() {
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, HOME_PAGE, 0, NORMAL_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UBI);
        ValidUbiEvent event = (ValidUbiEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(HOME_PAGE);
        assertThat(event.getPageName()).isEqualTo(HOME_PAGE_NAME);
        assertThat(event.getUrl()).isEqualTo(NORMAL_URL);
        assertThat(event.getReferer()).isEqualTo(GOOGLE_REF);
    }

    @Test
    public void extractCandidate_ubiIframe_invalid() {
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, IFRAME_PAGE, 0, NORMAL_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate).isNull();
    }

    @Test
    public void extractCandidate_ubiRdt_invalid() {
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, HOME_PAGE, 1, NORMAL_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate).isNull();
    }

    @Test
    public void extractCandidate_surfaceWeb_valid() {
        UniEvent uniEvent = getUniEvent(AUTOTRACK_WEB, HOME_PAGE, 0, NORMAL_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(SURFACE);
        ValidSurfaceEvent event = (ValidSurfaceEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(HOME_PAGE);
        assertThat(event.getPageName()).isEqualTo(HOME_PAGE_NAME);
        assertThat(event.getUrl()).isEqualTo(NORMAL_URL);
        assertThat(event.getReferer()).isEqualTo(GOOGLE_REF);
    }

    @Test
    public void extractCandidate_surfaceNative_invalid() {
        UniEvent uniEvent = getUniEvent(AUTOTRACK_NATIVE, HOME_PAGE, 0, NORMAL_URL, GOOGLE_REF);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate).isNull();
    }

    @Test
    public void extractCandidate_chocolate() {
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_CHNL, "1");
        payload.put(PAYLOAD_KEY_ROTID, String.valueOf(EPN_RID));
        payload.put(PAYLOAD_KEY_URL_MPRE, UrlUtils.encode(EPN_URL));
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, CHOCOLATE_PAGE, 0, GOOGLE_REF, payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UTP);
        UtpEvent event = (UtpEvent) candidate;
        assertThat(event.getChnl()).isEqualTo(1);
        assertThat(event.getRotId()).isEqualTo(EPN_RID);
        assertThat(event.getMpxChnlId()).isEqualTo(6);
        assertThat(event.getPageId()).isEqualTo(CHOCOLATE_PAGE);
        assertThat(event.getUrl()).isEqualTo(EPN_URL);
    }

    @Test
    public void extractCandidate_notification() {
        Map<String, String> payload = new HashMap<>();
        payload.put(PAYLOAD_KEY_PNACT, "1");
        UniEvent uniEvent = getUniEvent(UBI_NONBOT, NOTIFICATION_PAGE, 0, GOOGLE_REF, payload);
        TrafficSourceCandidate candidate = detector.extractCandidate(uniEvent);
        assertThat(candidate.getType()).isEqualTo(UTP);
        UtpEvent event = (UtpEvent) candidate;
        assertThat(event.getPageId()).isEqualTo(NOTIFICATION_PAGE);
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

    private static void mockPageLookup(TrafficSourceLookupManager lookupManager) {
        Map<Integer, Page > pageMap = new HashMap<>();
        pageMap.put(HOME_PAGE, getPage(HOME_PAGE, HOME_PAGE_NAME, 0));
        pageMap.put(CHOCOLATE_PAGE, getPage(CHOCOLATE_PAGE, "mktcollectionsvc__DefaultPage", 1));
        pageMap.put(IFRAME_PAGE, getPage(IFRAME_PAGE, IFRAME_PAGE_NAME, 1));
        when(lookupManager.getPageMap()).thenReturn(pageMap);
    }

    private static void mockRotationLookup(TrafficSourceLookupManager lookupManager) {
        Map<Long, DwMpxRotation> rotationMap = new HashMap<>();
        rotationMap.put(EPN_RID, getMpxRotation(EPN_RID, 6));
        when(lookupManager.getDwMpxRotationMap()).thenReturn(rotationMap);
    }



}
