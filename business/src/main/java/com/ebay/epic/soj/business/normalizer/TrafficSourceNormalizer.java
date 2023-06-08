package com.ebay.epic.soj.business.normalizer;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class TrafficSourceNormalizer extends FieldNormalizer<RawEvent, UniEvent> {
    @Override
    public void normalize(RawEvent src, UniEvent tar) throws Exception {
        tar.setEntityType(src.getEntityType());
        tar.setReferer(src.getReferer());
        tar.setPageUrl(src.getPageUrl());
        tar.setExperience(src.getExperience());
        tar.getPayload().putAll(src.getPayload());
    }
}
