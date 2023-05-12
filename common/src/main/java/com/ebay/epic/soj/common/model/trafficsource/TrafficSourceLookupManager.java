package com.ebay.epic.soj.common.model.trafficsource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TrafficSourceLookupManager {
    private static TrafficSourceLookupManager trafficSourceLookupManager =
            new TrafficSourceLookupManager();
    private Map<Long, DwMpxRotation> dwMpxRotationMap =
            Collections.unmodifiableMap(new HashMap<>());
    private Map<Integer, Page> pageMap =
            Collections.unmodifiableMap(new HashMap<>());

    private TrafficSourceLookupManager() {

    }

    public static TrafficSourceLookupManager getInstance() {
        return trafficSourceLookupManager;
    }

    public Map<Long, DwMpxRotation> getDwMpxRotationMap() {
        return dwMpxRotationMap;
    }

    public Map<Integer, Page> getPageMap() {
        return pageMap;
    }
}
