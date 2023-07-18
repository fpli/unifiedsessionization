package com.ebay.epic.soj.common.model.trafficsource;

import com.ebay.epic.soj.common.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
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

    public boolean allNull() {
        return firstValidSurfaceEvent == null &&
                firstValidUbiEvent == null &&
                firstDeeplinkActionEvent == null &&
                firstUtpEvent == null &&
                firstImbdEvent == null;
    }

    public String toJson() {
        try {
            String jsonStr = CommonUtils.objectMapper.writeValueAsString(this);
            return jsonStr;
        } catch (IOException e) {
            log.warn("failed to convert to json string", e);
        }
        return null;
    }
}
