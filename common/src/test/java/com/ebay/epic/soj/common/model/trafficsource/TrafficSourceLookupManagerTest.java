package com.ebay.epic.soj.common.model.trafficsource;

import org.junit.Test;

import java.util.Map;

import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.CHOCOLATE_PAGE;
import static org.assertj.core.api.Assertions.assertThat;

public class TrafficSourceLookupManagerTest {

    @Test
    public void testTrafficSourceLookup() {
        // The numbers here are based on 2023/06/23 snapshot.
        TrafficSourceLookupManager trafficSourceLookupManager =
                TrafficSourceLookupManager.getInstance();
        Map<Integer, Page> pageMap = trafficSourceLookupManager.getPageMap();
        assertThat(pageMap.size()).isEqualTo(16425);
        Page page1 = pageMap.get(CHOCOLATE_PAGE);
        assertThat(page1.getPageName()).isEqualTo("mktcollectionsvc__DefaultPage");
        assertThat(page1.getIframe()).isEqualTo(1);
        Map<Long, DwMpxRotation> dwMpxRotationMap =
                trafficSourceLookupManager.getDwMpxRotationMap();
        assertThat(dwMpxRotationMap.size()).isEqualTo(406587);
        DwMpxRotation rotation1 = dwMpxRotationMap.get(215791617356762562L);
        assertThat(rotation1.getMpxChnlId()).isEqualTo(2);
        DwMpxRotation rotation2 = dwMpxRotationMap.get(5158661111558342L);
        assertThat(rotation2.getMpxChnlId()).isEqualTo(19);
    }
}
