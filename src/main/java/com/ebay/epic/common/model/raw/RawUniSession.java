package com.ebay.epic.common.model.raw;

import lombok.Data;

@Data
public class RawUniSession {
  private String guid;
  private String globalSessionId;
  private Long absStartTimestamp;
  private Long absEndTimestamp;
  private Long sessionStartDt;

  public RawUniSession merge(RawUniSession uniSession) {
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
