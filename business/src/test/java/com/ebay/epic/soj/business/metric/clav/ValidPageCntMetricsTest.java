package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidPageCntMetricsTest {

    private ValidPageCntMetrics validPageCntMetrics;
    private UniEvent uniEvent;
    private ClavSession clavSession;

    @BeforeEach
    void setUp() {
        validPageCntMetrics = new ValidPageCntMetrics();
        uniEvent = mock(UniEvent.class);
        clavSession = new ClavSession();
    }

    @Test
    void processShouldIncrementValidPageCountWhenPageIsValidAndNotInList() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.getPageId()).thenReturn(123);

        validPageCntMetrics.process(uniEvent, clavSession);

        assertEquals(1, clavSession.getValidPageCount());
    }

    @Test
    void processShouldNotIncrementValidPageCountWhenPageIsInvalid() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(false);
        when(uniEvent.getPageId()).thenReturn(123);

        validPageCntMetrics.process(uniEvent, clavSession);

        assertEquals(0, clavSession.getValidPageCount());
    }

    @Test
    void processShouldNotIncrementValidPageCountWhenPageIdIsInList() throws Exception {
        when(uniEvent.isClavValidPage()).thenReturn(true);
        when(uniEvent.getPageId()).thenReturn(2557882);

        validPageCntMetrics.process(uniEvent, clavSession);

        assertEquals(0, clavSession.getValidPageCount());
    }
}
