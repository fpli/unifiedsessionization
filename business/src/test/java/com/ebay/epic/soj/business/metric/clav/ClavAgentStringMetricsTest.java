package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClavAgentStringMetricsTest {

    ClavAgentStringMetrics clavAgentStringMetrics;
    UniEvent uniEvent;
    ClavSession clavSession;

    @BeforeEach
    void setUp() throws Exception {
        clavAgentStringMetrics = new ClavAgentStringMetrics();
        clavAgentStringMetrics.init();
        uniEvent = new UniEvent();
        clavSession = new ClavSession();
    }

    @Test
    void test_process_ua_change() throws Exception {
        uniEvent.setIframe(false);
        uniEvent.setUserAgent("new agent string");

        clavAgentStringMetrics.process(uniEvent, clavSession);

        assertThat(clavSession.getUserAgent()).isEqualTo("new agent string");
    }

    @Test
    void test_process_ua_change_early_event() throws Exception {
        uniEvent.setIframe(false);
        uniEvent.setUserAgent("new user agent");
        uniEvent.setEventTs(999L);

        clavSession.setUserAgent("old user agent");
        clavSession.setStartTimestamp(1000L);

        clavAgentStringMetrics.process(uniEvent, clavSession);
        assertThat(clavSession.getUserAgent()).isEqualTo("new user agent");
    }

    @Test
    void test_process_ua_no_change() throws Exception {
        uniEvent.setIframe(true);
        uniEvent.setUserAgent("new user agent");
        clavSession.setUserAgent("old user agent");
        clavAgentStringMetrics.process(uniEvent, clavSession);
        assertThat(clavSession.getUserAgent()).isEqualTo("old user agent");
    }
}