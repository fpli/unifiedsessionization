package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageIdMetricsTest {

    private PageIdMetrics pageIdMetrics;
    private UniEvent uniEvent;
    private ClavSession clavSession;

    @BeforeEach
    void setUp() {
        pageIdMetrics = new PageIdMetrics();
        uniEvent = mock(UniEvent.class);
        clavSession = new ClavSession();
    }

    @Test
    void processShouldSetStartPageIdWhenValidAndEarly() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.getPageId()).thenReturn(123);

        pageIdMetrics.process(uniEvent, clavSession);

        assertEquals(123, clavSession.getStartPageId());
    }

    @Test
    void processShouldNotSetStartPageIdWhenInvalid() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(false);
        when(uniEvent.getPageId()).thenReturn(123);

        pageIdMetrics.process(uniEvent, clavSession);

        assertEquals(0, clavSession.getStartPageId());
    }

    @Test
    void processShouldSetExitPageIdWhenValidAndLate() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.getPageId()).thenReturn(123);

        pageIdMetrics.process(uniEvent, clavSession);

        assertEquals(123, clavSession.getExitPageId());
    }

    @Test
    void processShouldNotSetExitPageIdWhenInvalid() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(false);
        when(uniEvent.getPageId()).thenReturn(123);

        pageIdMetrics.process(uniEvent, clavSession);

        assertEquals(0, clavSession.getExitPageId());
    }
}