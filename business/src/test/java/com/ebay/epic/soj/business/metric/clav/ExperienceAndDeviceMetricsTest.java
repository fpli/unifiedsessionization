package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


//skip first as local env cannot visit prod dds env
@Disabled
class ExperienceAndDeviceMetricsTest {

    ExperienceAndDeviceMetrics experienceAndDeviceMetrics;
    UniEvent uniEvent;
    ClavSession clavSession;


    @BeforeEach
    void setUp() throws Exception {
        experienceAndDeviceMetrics = new ExperienceAndDeviceMetrics();
        experienceAndDeviceMetrics.init();
        uniEvent = new UniEvent();
        clavSession = new ClavSession();
    }

    @Test
    void test_process_pc_browser() throws Exception {
        String pcUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";
        clavSession.setUserAgent(pcUA);
        experienceAndDeviceMetrics.process(uniEvent, clavSession);

        assertThat(clavSession.getExperienceLevel2()).isEqualTo("Browser: Core site");
        assertThat(clavSession.getDeviceTypeLevel2()).isEqualTo("PC");
    }

    @Test
    void test_process_mweb() throws Exception {
        String mwebUA = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1";
        clavSession.setUserAgent(mwebUA);
        experienceAndDeviceMetrics.process(uniEvent, clavSession);

        assertThat(clavSession.getExperienceLevel2()).isEqualTo("Browser: mWeb");
        assertThat(clavSession.getDeviceTypeLevel2()).isEqualTo("Mobile: Phone");
    }
}