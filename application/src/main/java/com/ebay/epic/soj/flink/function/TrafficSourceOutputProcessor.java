package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.common.model.UniSession;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidates;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceDetails;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TrafficSourceOutputProcessor implements Serializable {

    public static final String OTHERS_TRAFFIC_SOURCE_CANDIDATES = "trafficSourceCandidates";

    public void output(RawUniSession rawUniSession, UniSession uniSession) {
        try {
            // traffic source details
            TrafficSourceDetails trafficSourceDetails = rawUniSession.getTrafficSourceDetails();
            if (trafficSourceDetails != null &&
                    trafficSourceDetails.getTrafficSourceLevel3() != null) {
                uniSession.setTrafficSourceDetails(trafficSourceDetails.toMap());
            }

            // traffic source candidates for debug
            TrafficSourceCandidates trafficSourceCandidates =
                    rawUniSession.getTrafficSourceCandidates();
            if (trafficSourceCandidates != null && !trafficSourceCandidates.allNull()) {
                Map<String, String> others = uniSession.getOthers();
                if (others == null) {
                    others = new HashMap<>();
                    uniSession.setOthers(others);
                }
                others.put(OTHERS_TRAFFIC_SOURCE_CANDIDATES, trafficSourceCandidates.toJson());
            }
        } catch (Exception e) {
            log.warn("failed to output traffic source", e);
        }
    }
}
