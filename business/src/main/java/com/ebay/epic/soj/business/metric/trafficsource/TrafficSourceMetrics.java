package com.ebay.epic.soj.business.metric.trafficsource;


import com.ebay.epic.soj.business.metric.FieldMetrics;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.DeeplinkActionEvent;
import com.ebay.epic.soj.common.model.trafficsource.ImbdEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidate;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidateType;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidates;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceDetails;
import com.ebay.epic.soj.common.model.trafficsource.UtpEvent;
import com.ebay.epic.soj.common.model.trafficsource.ValidSurfaceEvent;
import com.ebay.epic.soj.common.model.trafficsource.ValidUbiEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrafficSourceMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

    private TrafficSourceDetector trafficSourceDetector;

    @Override
    public void init() throws Exception {
        trafficSourceDetector = new TrafficSourceDetector();
    }

    @Override
    public void start(UniSessionAccumulator uniSessionAccumulator) throws Exception {

    }

    @Override
    public void process(UniEvent uniEvent, UniSessionAccumulator uniSessionAccumulator)
            throws Exception {
        RawUniSession rawUniSession = uniSessionAccumulator.getUniSession();
        TrafficSourceCandidates trafficSourceCandidates =
                rawUniSession.getTrafficSourceCandidates();
        TrafficSourceCandidate trafficSourceCandidate = null;
        try {
            trafficSourceCandidate = trafficSourceDetector.extractCandidate(uniEvent);
        } catch (Exception e) {
            log.warn("failed to extract traffic source candidate", e);
        }
        if (trafficSourceCandidate != null) {
            // events in streaming are out of order, we need to update first event for traffic
            // source candidates according to event timestamp
            try {
                updateTrafficSourceCandidates(trafficSourceCandidate, trafficSourceCandidates);
            } catch (Exception e) {
                log.warn("failed to update traffic source candidate", e);
            }
        }
    }

    private void updateTrafficSourceCandidates(
            TrafficSourceCandidate trafficSourceCandidate,
            TrafficSourceCandidates trafficSourceCandidates) {
        if (trafficSourceCandidate.getType() == TrafficSourceCandidateType.SURFACE) {
            if (trafficSourceCandidates.getFirstValidSurfaceEvent() == null ||
                    (trafficSourceCandidate.getEventTimestamp() < trafficSourceCandidates
                            .getFirstValidSurfaceEvent().getEventTimestamp())) {
                trafficSourceCandidates.setFirstValidSurfaceEvent(
                        (ValidSurfaceEvent) trafficSourceCandidate);
            }
        } else if (trafficSourceCandidate.getType() == TrafficSourceCandidateType.UBI) {
            if (trafficSourceCandidates.getFirstValidUbiEvent() == null ||
                    (trafficSourceCandidate.getEventTimestamp() < trafficSourceCandidates
                            .getFirstValidUbiEvent().getEventTimestamp())) {
                trafficSourceCandidates.setFirstValidUbiEvent(
                        (ValidUbiEvent) trafficSourceCandidate);
            }
        } else if (trafficSourceCandidate.getType() == TrafficSourceCandidateType.DEEPLINK) {
            if (trafficSourceCandidates.getFirstDeeplinkActionEvent() == null ||
                    (trafficSourceCandidate.getEventTimestamp() < trafficSourceCandidates
                            .getFirstDeeplinkActionEvent().getEventTimestamp())) {
                trafficSourceCandidates.setFirstDeeplinkActionEvent(
                        (DeeplinkActionEvent) trafficSourceCandidate);
            }
        } else if (trafficSourceCandidate.getType() == TrafficSourceCandidateType.UTP) {
            if (trafficSourceCandidates.getFirstUtpEvent() == null ||
                    (trafficSourceCandidate.getEventTimestamp() < trafficSourceCandidates
                            .getFirstUtpEvent().getEventTimestamp())) {
                trafficSourceCandidates.setFirstUtpEvent(
                        (UtpEvent) trafficSourceCandidate);
            }
        } else if (trafficSourceCandidate.getType() == TrafficSourceCandidateType.IMBD) {
            if (trafficSourceCandidates.getFirstImbdEvent() == null ||
                    (trafficSourceCandidate.getEventTimestamp() < trafficSourceCandidates
                            .getFirstImbdEvent().getEventTimestamp())) {
                trafficSourceCandidates.setFirstImbdEvent(
                        (ImbdEvent) trafficSourceCandidate);
            }
        }

    }

    @Override
    public void end(UniSessionAccumulator uniSessionAccumulator) throws Exception {
        RawUniSession rawUniSession = uniSessionAccumulator.getUniSession();
        TrafficSourceCandidates trafficSourceCandidates =
                rawUniSession.getTrafficSourceCandidates();
        if (trafficSourceCandidates != null && trafficSourceCandidates.hasAtLeastOneCandidate()) {
            TrafficSourceDetails trafficSourceDetails = null;
            try {
                trafficSourceDetails = trafficSourceDetector.determineTrafficSource(
                        trafficSourceCandidates);
            } catch (Exception e) {
                log.warn("failed to determine traffic source", e);
            }
            if (trafficSourceDetails != null) {
                rawUniSession.setTrafficSourceDetails(trafficSourceDetails);
            }
        }
    }
}
