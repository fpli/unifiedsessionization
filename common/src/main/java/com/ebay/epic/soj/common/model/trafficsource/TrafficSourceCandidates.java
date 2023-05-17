package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class TrafficSourceCandidates {
    private ValidSurfaceEvent firstValidSurfaceEvent;
    private ValidUbiEvent firstValidUbiEvent;
    private DeeplinkActionEvent firstDeeplinkActionEvent;
    private UtpEvent firstUtpEvent;
    private ImbdEvent firstImbdEvent;

    public boolean hasAtLeastOneCandidate() {
        return firstValidSurfaceEvent != null ||
                firstValidUbiEvent != null;
    }
}
