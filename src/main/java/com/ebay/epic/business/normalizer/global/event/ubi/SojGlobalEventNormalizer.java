package com.ebay.epic.business.normalizer.global.event.ubi;

import com.ebay.epic.business.normalizer.global.event.ubi.domain.A2CNormalizer;
import com.ebay.epic.business.normalizer.global.event.ubi.domain.HomePageNormalizer;
import com.ebay.epic.business.normalizer.global.event.ubi.domain.SearchNormalizer;
import com.ebay.epic.business.normalizer.global.event.ubi.domain.ViewItemNormalizer;
import com.ebay.epic.business.normalizer.RecordNormalizer;
import com.ebay.epic.common.model.avro.GlobalEvent;
import com.ebay.epic.common.model.avro.SojEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SojGlobalEventNormalizer extends RecordNormalizer<SojEvent, GlobalEvent> {

    @Override
    public void initFieldNormalizers() {
        // TODO: 2022/3/7  add fieldNormalizers
        addFieldNormalizer(new CommonNormalizer());
        addFieldNormalizer(new BravoosMetaNormalizer());
        addFieldNormalizer(new A2CNormalizer());
        addFieldNormalizer(new HomePageNormalizer());
        addFieldNormalizer(new SearchNormalizer());
        addFieldNormalizer(new ViewItemNormalizer());
    }

}
