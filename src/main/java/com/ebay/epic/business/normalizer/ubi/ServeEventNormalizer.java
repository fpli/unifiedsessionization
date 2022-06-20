package com.ebay.epic.business.normalizer.ubi;

import com.ebay.epic.business.normalizer.ubi.domain.*;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.business.normalizer.RecordNormalizer;
import com.ebay.epic.sojourner.business.normalizer.ubi.domain.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServeEventNormalizer extends RecordNormalizer<SojEvent, UniTrackingEvent> {

    @Override
    public void initFieldNormalizers() {
        // TODO: 2022/3/7  add fieldNormalizers
        addFieldNormalizer(new CommonNormalizer());
        addFieldNormalizer(new A2CNormalizer());
        addFieldNormalizer(new AskQuestionNormalizer());
        addFieldNormalizer(new BinNormalizer());
        addFieldNormalizer(new BidNormalizer());
        addFieldNormalizer(new BrowseNodeNormalizer());
        addFieldNormalizer(new CheckoutNormalizer());
        addFieldNormalizer(new HomePageNormalizer());
        addFieldNormalizer(new OfferNormalizer());
        addFieldNormalizer(new SearchNormalizer());
        addFieldNormalizer(new ViewItemNormalizer());
        addFieldNormalizer(new WatchNormalizer());
    }

}
