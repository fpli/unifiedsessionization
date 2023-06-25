package com.ebay.epic.soj.common.model.trafficsource;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TrafficSourceLookupManagerTest {

    @Test
    public void testTrafficSourceLookup() {
        // The numbers here are based on 2023/06/23 snapshot.
        TrafficSourceLookupManager trafficSourceLookupManager =
                TrafficSourceLookupManager.getInstance();
        Map<Integer, Page> pageMap = trafficSourceLookupManager.getPageMap();
        assertThat(pageMap.size()).isEqualTo(16425);
        Map<Long, DwMpxRotation> dwMpxRotationMap =
                trafficSourceLookupManager.getDwMpxRotationMap();
        assertThat(dwMpxRotationMap.size()).isEqualTo(406587);
    }
}
