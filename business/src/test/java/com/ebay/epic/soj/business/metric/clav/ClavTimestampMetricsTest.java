package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClavTimestampMetricsTest {

    private ClavTimestampMetrics clavTimestampMetrics;
    private UniEvent uniEvent;
    private ClavSession clavSession;

    @BeforeEach
    void setUp() {
        clavTimestampMetrics = new ClavTimestampMetrics();
        uniEvent = mock(UniEvent.class);
        clavSession = new ClavSession();
    }

    @Test
    void processShouldSetStartTimestampWhenValidAndEarly() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.getEventTs()).thenReturn(123L);

        clavTimestampMetrics.process(uniEvent, clavSession);

        assertEquals(123L, clavSession.getStartTimestamp());
    }

    @Test
    void processShouldNotSetStartTimestampWhenInvalid() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(false);
        when(uniEvent.getEventTs()).thenReturn(123L);

        clavTimestampMetrics.process(uniEvent, clavSession);

        assertEquals(0, clavSession.getStartTimestamp());
    }

    @Test
    void processShouldSetExitTimestampWhenValidAndLate() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.getEventTs()).thenReturn(123L);

        clavTimestampMetrics.process(uniEvent, clavSession);

        assertEquals(123L, clavSession.getExitTimestamp());
    }

    @Test
    void processShouldNotSetExitTimestampWhenInvalid() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(false);
        when(uniEvent.getEventTs()).thenReturn(123L);

        clavTimestampMetrics.process(uniEvent, clavSession);

        assertEquals(0, clavSession.getExitTimestamp());
    }

    @Test
    void processShouldSetAbsStartTimestampWhenEarly() throws Exception {
        when(uniEvent.getEventTs()).thenReturn(123L);

        clavTimestampMetrics.process(uniEvent, clavSession);

        assertEquals(123L, clavSession.getAbsStartTimestamp());
    }

    @Test
    void processShouldSetAbsEndTimestampWhenLate() throws Exception {
        when(uniEvent.getEventTs()).thenReturn(123L);

        clavTimestampMetrics.process(uniEvent, clavSession);

        assertEquals(123L, clavSession.getAbsEndTimestamp());
    }
}