package com.ebay.epic.soj.common.model.raw;

import com.ebay.epic.soj.common.enums.TrafficSource;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidates;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceDetails;
import lombok.Data;

import java.util.Map;
import java.util.Optional;
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
    private Long startTimestamp;
    private Long startTimestampNOIFRAMERDT;
    private TrafficSource trafficSource = TrafficSource.EMPTY;
    private Long trafficSourceTs;
    private TrafficSourceCandidates trafficSourceCandidates = new TrafficSourceCandidates();
    private TrafficSourceDetails trafficSourceDetails;
    private Set<String> ubiSessIds = new CopyOnWriteArraySet<>();
    private Set<Long> ubiSessSkeys = new CopyOnWriteArraySet<>();
    private Set<Long> autotrackSessIds = new CopyOnWriteArraySet<>();
    private Set<Long> autotrackSessSkeys = new CopyOnWriteArraySet<>();
    private Map<String, String> others = new ConcurrentHashMap<>();
    private Set<Integer> surfaceBotList = new CopyOnWriteArraySet<>();
    private Set<Integer> ubiBotList = new CopyOnWriteArraySet<>();
    private Set<Integer> sutpBotList = new CopyOnWriteArraySet<>();
    // For traffic source
    private Map<String, String> trafficSourceDtl = new ConcurrentHashMap<>();

    // For unisession extended for caleb session usage
    private String userId;
    private Integer firstAppId;
    private Integer appId;
    private Integer cobrand;
    private Integer firstCobrand;
    private String userAgent;
    private String experience;
    private String experienceLevel1;
    private String experienceLevel2;
    private Long endTimestamp;
    private Map<UbiKey, ClavSession> clavSessionMap = new ConcurrentHashMap<>();

    private Integer eventCnt;
    private Integer ubiCnt;
    private Integer utpCnt;
    private Integer surfaceCnt;
    private Integer roiCnt;

    private Long botSignature;

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
                && this.trafficSourceTs > uniSession.getTrafficSourceTs())) {
            this.trafficSource = uniSession.getTrafficSource();
            this.trafficSourceTs = uniSession.getTrafficSourceTs();
        }

        //TODO for traffic source

        if (this.getStartTimestamp() == null && uniSession.getStartTimestamp() != null) {
            refreshGlobalExtended(uniSession);
        } else if (uniSession.getStartTimestamp() != null && this.getStartTimestamp() > uniSession
                .getStartTimestamp()) {
            refreshGlobalExtended(uniSession);
        }
        if (this.getEndTimestamp() == null && uniSession.getEndTimestamp() != null) {
            this.setEndTimestamp(uniSession.getEndTimestamp());
        } else if (uniSession.getEndTimestamp() != null && this.getEndTimestamp() < uniSession
                .getEndTimestamp()) {
            this.setEndTimestamp(uniSession.getEndTimestamp());
        }

        //TODO for ClavSession
        for (Map.Entry<UbiKey, ClavSession> clavSession : clavSessionMap.entrySet()) {
            if (uniSession.getClavSessionMap().containsKey(clavSession.getKey())) {
                refreshClavSessionExtended(clavSession.getValue(),
                        uniSession.getClavSessionMap().get(clavSession.getKey()));
            }
        }
        for (Map.Entry<UbiKey, ClavSession> clavSession : uniSession.getClavSessionMap().entrySet()) {
            clavSessionMap.putIfAbsent(clavSession.getKey(), clavSession.getValue());
        }

        //TODO for unified session extend


        this.eventCnt = add(this.eventCnt, uniSession.getEventCnt());
        this.ubiCnt = add(this.ubiCnt, uniSession.getUbiCnt());
        this.utpCnt = add(this.utpCnt, uniSession.getUtpCnt());
        this.surfaceCnt = add(this.surfaceCnt, uniSession.getSurfaceCnt());
        this.roiCnt = add(this.roiCnt, uniSession.getSurfaceCnt());
        this.botSignature = mergeBotSignature(this.botSignature, uniSession.getBotSignature());
        return this;
    }

    private void refreshGlobalExtended(RawUniSession uniSession) {
        this.setStartTimestamp(uniSession.getStartTimestamp());
        this.setUserAgent(uniSession.getUserAgent());
        this.setUserId(uniSession.getUserId());
        this.setCobrand(uniSession.getCobrand());
        this.setExperience(uniSession.getExperience());
        this.setExperienceLevel1(uniSession.getExperienceLevel1());
        this.setExperienceLevel2(uniSession.getExperienceLevel2());
        this.setFirstAppId(uniSession.getFirstAppId());
    }

    private void refreshClavSessionExtended(ClavSession src, ClavSession tgt) {
        refreshCommon(src, tgt);
        Long minStart = src.getStartTimestamp();
        if (src.getStartTimestamp() == null && tgt.getStartTimestamp() != null) {
            src.setStartTimestamp(tgt.getStartTimestamp());
            src.setStartPageId(tgt.getStartPageId());
            src.setSiteId(tgt.getSiteId());
            src.setSessionId(tgt.getSessionId());
            minStart = tgt.getStartTimestamp();
        } else if (src.getStartTimestamp() != null && src.getStartTimestamp() > tgt
                .getStartTimestamp()) {
            src.setStartTimestamp(tgt.getStartTimestamp());
            src.setStartPageId(tgt.getStartPageId());
            src.setSiteId(tgt.getSiteId());
            minStart = tgt.getStartTimestamp();
            src.setSessionId(tgt.getSessionId());
        }
        Long maxEnd = src.getExitTimestamp();
        if (src.getExitTimestamp() == null && tgt.getExitTimestamp() != null) {
            src.setExitTimestamp(tgt.getExitTimestamp());
            src.setExitPageId(tgt.getExitPageId());
            maxEnd = tgt.getExitTimestamp();
        } else if (tgt.getExitTimestamp() != null && src.getExitTimestamp() < tgt
                .getExitTimestamp()) {
            src.setExitTimestamp(tgt.getExitTimestamp());
            src.setExitPageId(tgt.getExitPageId());
            maxEnd = tgt.getExitTimestamp();
        }
        if (maxEnd != null && minStart != null) {
            src.setDuration(maxEnd - minStart);
        }
    }

    private void refreshCommon(ClavSession src, ClavSession tgt) {
        src.setGrCount(Optional.of(src.getGrCount()).orElse(0)
                + Optional.of(tgt.getGrCount()).orElse(0));
        src.setGr1Count(Optional.of(src.getGr1Count()).orElse(0)
                + Optional.of(tgt.getGr1Count()).orElse(0));
        src.setSigninCount(Optional.of(src.getSigninCount()).orElse(0)
                + Optional.of(tgt.getSigninCount()).orElse(0));
        src.setMyebayCount(Optional.of(src.getMyebayCount()).orElse(0)
                + Optional.of(tgt.getMyebayCount()).orElse(0));
        src.setHomepageCount(Optional.of(src.getHomepageCount()).orElse(0)
                + Optional.of(tgt.getHomepageCount()).orElse(0));
        src.setBotFlag(src.getBotFlag() | tgt.getBotFlag());
        src.getOthers().putAll(tgt.getOthers());
        src.setViCount(Optional.of(src.getViCount()).orElse(0)
                + Optional.of(tgt.getViCount()).orElse(0));
        src.setValidPageCount(Optional.of(src.getValidPageCount()).orElse(0)
                + Optional.of(tgt.getValidPageCount()).orElse(0));
    }

    private Integer add(Integer a, Integer b) {
        return Optional.ofNullable(a).orElse(0) + Optional.ofNullable(b).orElse(0);
    }

    private Long mergeBotSignature(Long a, Long b) {
        Long botSignature = Optional.ofNullable(a).orElse(0L);
        botSignature |= Optional.ofNullable(b).orElse(0L);
        return botSignature;
    }

}
