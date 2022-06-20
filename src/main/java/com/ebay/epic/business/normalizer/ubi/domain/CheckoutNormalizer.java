package com.ebay.epic.business.normalizer.ubi.domain;

import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UniTrackingEvent;

public final class CheckoutNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
        return super.accept(src, tar);
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
    }

}
