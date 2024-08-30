package com.ebay.epic.soj.business.normalizer;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class PartialValidPageNormalizer extends FieldNormalizer<RawEvent, UniEvent> {
    @Override
    public boolean accept(RawEvent src) {
        return src.getEventType() == EventType.UBI_NONBOT || src.getEventType() == EventType.UBI_BOT;
    }

    @Override
    public void normalize(RawEvent src, UniEvent tar) throws Exception {
        if (src.getPayload().containsKey("spvpf")) {
            tar.setClavValidPage(true);
        } else {
            tar.setClavValidPage(false);
        }
    }
}
