package com.ebay.epic.soj.common.model.raw;

import com.ebay.epic.soj.common.enums.TrafficSource;
import com.ebay.epic.soj.common.model.ClavSession;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
public class RawUniSession {
    private String guid;
    private String globalSessionId;
    private Long absStartTimestamp;
    private Long absEndTimestamp;
    private Long sessionStartDt;
    private Long startTimestamp;
    private TrafficSource trafficSource = TrafficSource.EMPTY;
    private Long trafficSourceTs;
    private Set<String> ubiSessIds = new CopyOnWriteArraySet<>();
    private Set<Long> ubiSessSkeys = new CopyOnWriteArraySet<>();
    private Set<Long> autotrackSessIds = new CopyOnWriteArraySet<>();
    private Set<Long> autotrackSessSkeys = new CopyOnWriteArraySet<>();
    private Map<String, String> others = new ConcurrentHashMap<>();
    private Set<Integer> surfaceBotList= new CopyOnWriteArraySet<>();
    private Set<Integer> ubiBotList= new CopyOnWriteArraySet<>();
    private Set<Integer> sutpBotList= new CopyOnWriteArraySet<>();
    // For traffic source
    private Map<String,String> trafficSourceDtl = new ConcurrentHashMap<>();

    // For unisession extended for caleb session usage
    private String userId;
    private Integer firstAppId;
    private Integer cobrand;
    private String userAgent;
    private String experience;
    private String experienceLevel1;
    private String experienceLevel2;
    private Map<String,ClavSession> clavSessionMap = new ConcurrentHashMap<>();

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
        this.surfaceBotList.addAll(uniSession.getSurfaceBotList());
        this.ubiBotList.addAll(uniSession.getUbiBotList());
        this.sutpBotList.addAll(uniSession.getSutpBotList());
        if (this.trafficSourceTs == null || (uniSession.getTrafficSourceTs() != null
                && this.trafficSourceTs > uniSession.getTrafficSourceTs()))
        {
            this.trafficSource = uniSession.getTrafficSource();
            this.trafficSourceTs = uniSession.getTrafficSourceTs();
        }

        //TODO for traffic source

        //TODO for unified session extend

        return this;
    }
}
