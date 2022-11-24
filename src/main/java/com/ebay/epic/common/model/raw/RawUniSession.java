package com.ebay.epic.common.model.raw;

import com.ebay.epic.common.enums.TrafficSource;
import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
public class RawUniSession {
    private String guid;
    private String globalSessionId;
    private Long absStartTimestamp;
    private Long absEndTimestamp;
    private Long sessionStartDt;
    private TrafficSource trafficSource = TrafficSource.EMPTY;
    private Long trafficSourceTs;
    private Set<String> ubiSessIds = new CopyOnWriteArraySet<>();
    private Set<Long> ubiSessSkeys = new CopyOnWriteArraySet<>();
    private Set<Long> autotrackSessIds = new CopyOnWriteArraySet<>();
    private Set<Long> autotrackSessSkeys = new CopyOnWriteArraySet<>();
    private Map<String, String> others = new ConcurrentHashMap<>();

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
        this.others.putAll(uniSession.getOthers());
        this.ubiSessIds.addAll(uniSession.getUbiSessIds());
        this.autotrackSessIds.addAll(uniSession.getAutotrackSessIds());
        this.ubiSessSkeys.addAll(uniSession.getUbiSessSkeys());
        this.autotrackSessSkeys.addAll(uniSession.getAutotrackSessSkeys());
        if (this.trafficSourceTs == null || (uniSession.getTrafficSourceTs() != null
                && this.trafficSourceTs > uniSession.getTrafficSourceTs()))
        {
            this.trafficSource = uniSession.getTrafficSource();
            this.trafficSourceTs = uniSession.getTrafficSourceTs();
        }
        return this;
    }
}
