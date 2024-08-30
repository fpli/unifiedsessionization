package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CobrandMetricsTest {

    private CobrandMetrics cobrandMetrics;
    private UniEvent uniEvent;
    private ClavSession clavSession;

    @BeforeEach
    void setUp() {
        cobrandMetrics = new CobrandMetrics();
        uniEvent = mock(UniEvent.class);
        clavSession = new ClavSession();
    }

    @Test
    void processShouldSetCobrandWhenValid() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.isValid()).thenReturn(true);
        when(uniEvent.getCobrand()).thenReturn("123");

        cobrandMetrics.process(uniEvent, clavSession);

        assertEquals(123, clavSession.getCobrand());
    }

    @Test
    void processShouldNotSetCobrandWhenInvalid() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(false);
        when(uniEvent.isValid()).thenReturn(true);
        when(uniEvent.getCobrand()).thenReturn("123");

        cobrandMetrics.process(uniEvent, clavSession);

        assertNull(clavSession.getCobrand());
    }

    @Test
    void processShouldNotSetCobrandWhenCobrandIsNull() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.isValid()).thenReturn(true);
        when(uniEvent.getCobrand()).thenReturn(null);

        cobrandMetrics.process(uniEvent, clavSession);

        assertNull(clavSession.getCobrand());
    }
}