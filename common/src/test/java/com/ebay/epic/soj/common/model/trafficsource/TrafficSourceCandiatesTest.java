package com.ebay.epic.soj.common.model.trafficsource;

import org.junit.Test;

public class TrafficSourceCandiatesTest {

    @Test
    public void testOutputSession() {
        TrafficSourceCandidates tsc1 = new TrafficSourceCandidates();
        tsc1.toJson();
    }
}
