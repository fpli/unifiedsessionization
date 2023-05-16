package com.ebay.epic.soj.business.normalizer;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class ClavValidPageNormalizer extends FieldNormalizer<RawEvent, UniEvent> {

    @Override
    public boolean accept(RawEvent src) {
        //TODO need to check is ubi event? if yes, how?
        return true;
    }

    @Override
    public void normalize(RawEvent src, UniEvent tar) throws Exception {
        if (checkIsValidEvent(src)) {
            tar.setClavValidPage(true);
        }
    }

    private boolean checkIsValidEvent(RawEvent src) {
        // TODO
        return true;
    }

}
