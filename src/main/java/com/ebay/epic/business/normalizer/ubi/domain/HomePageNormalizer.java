package com.ebay.epic.business.normalizer.ubi.domain;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.PageType;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.common.constant.SubPageType;
import com.ebay.epic.utils.ExtractTag;
import com.google.common.collect.ImmutableSet;

public class HomePageNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
        return acceptBoolean(ImmutableSet.of(2481888).contains(tar.getPageId()));
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.HOME_PAGE);
        tar.setSubPageType(SubPageType.OTHER);

        // Homepage
        tar.getBizDtl().put(Field.F_LEAF_FIRST_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_LF_CAT));
        tar.getBizDtl().put(Field.F_APPLIED_ASPECT, ExtractTag.extract(tar.getPayload(), Tag.T_AA));
    }

}