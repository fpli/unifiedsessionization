package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class TrafficSourceCandidate {
    protected long eventTimestamp;

    public TrafficSourceCandidateType getType() {
        if (this.getClass().getName().equals(ValidSurfaceEvent.class.getName())) {
            return TrafficSourceCandidateType.SURFACE;
        } else if (this.getClass().getName().equals(ValidUbiEvent.class.getName())) {
            return TrafficSourceCandidateType.UBI;
        } else if (this.getClass().getName().equals(DeeplinkActionEvent.class.getName())) {
            return TrafficSourceCandidateType.DEEPLINK;
        } else if (this.getClass().getName().equals(UtpEvent.class.getName())) {
            return TrafficSourceCandidateType.UTP;
        } else if (this.getClass().getName().equals(ImbdEvent.class.getName())) {
            return TrafficSourceCandidateType.IMBD;
        } else {
            // this should never happen
            return null;
        }
    }
}
