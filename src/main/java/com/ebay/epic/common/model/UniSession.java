package com.ebay.epic.common.model;

import lombok.Data;

import java.util.Map;

@Data
public class UniSession {
    private String guid;
    private String globalSessionId;
    private Long absStartTimestamp;
    private Long absEndTimestamp;
    private Long sessionStartDt;

    public UniSession merge(UniSession uniSession) {
        if (this.getAbsStartTimestamp() == null && uniSession.getAbsStartTimestamp() != null) {
            this.setAbsStartTimestamp(uniSession.getAbsStartTimestamp());
        } else if (uniSession.getAbsStartTimestamp() != null && this.getAbsStartTimestamp() > uniSession
                .getAbsStartTimestamp()) {
            this.setAbsStartTimestamp(uniSession.getAbsStartTimestamp());
            this.setGlobalSessionId(uniSession.getGlobalSessionId());
            this.setSessionStartDt(uniSession.getSessionStartDt());
        }
        if (this.getAbsEndTimestamp() == null && uniSession.getAbsEndTimestamp() != null) {
            this.setAbsEndTimestamp(uniSession.getAbsEndTimestamp());
        } else if (uniSession.getAbsEndTimestamp() != null && this.getAbsEndTimestamp() < uniSession
                .getAbsEndTimestamp()) {
            this.setAbsEndTimestamp(uniSession.getAbsEndTimestamp());
        }
        return this;
    }
}
