package com.ebay.epic.business.normalizer.global.event.ubi.domain;

import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.EventPrimaryAsset;
import com.ebay.epic.common.model.avro.GlobalEvent;
import com.ebay.epic.common.model.avro.SojEvent;
import com.ebay.epic.utils.ExtractTag;
import com.google.common.collect.ImmutableSet;

import static com.ebay.epic.business.constant.ubi.domain.Field.F_APPLIED_ASPECT;
import static com.ebay.epic.business.constant.ubi.domain.Field.F_LEAF_FIRST_CATEGORY_ID;
import static com.ebay.epic.business.constant.ubi.domain.Tag.T_AA;
import static com.ebay.epic.business.constant.ubi.domain.Tag.T_LF_CAT;
import static com.ebay.epic.utils.MapPutIf.putIfNotBlankStr;

public class HomePageNormalizer extends AcceptorNormalizer<SojEvent, GlobalEvent> {

    @Override
    public int accept(SojEvent src, GlobalEvent tar) {
        return acceptBoolean(ImmutableSet.of(2481888).contains(tar.getPageId()));
    }

    @Override
    public void update(int code, SojEvent src, GlobalEvent tar) {
        tar.setEventPrimaryAsset(EventPrimaryAsset.HOME_PAGE);

        // Homepage
        putIfNotBlankStr(tar.getBizDtl(), F_LEAF_FIRST_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), T_LF_CAT));
        putIfNotBlankStr(tar.getBizDtl(), F_APPLIED_ASPECT, ExtractTag.extract(tar.getPayload(), T_AA));
    }

}