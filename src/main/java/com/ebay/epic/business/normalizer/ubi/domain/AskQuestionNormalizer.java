package com.ebay.epic.business.normalizer.ubi.domain;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.PageType;
import com.ebay.epic.common.constant.SubPageType;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.utils.ExtractTag;
import com.google.common.collect.ImmutableSet;

public class AskQuestionNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
        return acceptBoolean(ImmutableSet.of(2356359, 2065054, 2054175, 2053246, 2495288, 2495285, 2472946, 2485308).contains(tar.getPageId()));
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.ASK_QUESTION);
        tar.setSubPageType(SubPageType.OTHER);

        // ASQ
        tar.getBizDtl().put(Field.F_INDEX, ExtractTag.extract(tar.getPayload(), Tag.T_INDEX));
        tar.getBizDtl().put(Field.F_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_COUNT));
        tar.getBizDtl().put(Field.F_ACTION_NAME, ExtractTag.extract(tar.getPayload(), Tag.T_AN));
        tar.getBizDtl().put(Field.F_TRANSACTION_ID, ExtractTag.extract(tar.getPayload(), Tag.T_TRID));
        tar.getBizDtl().put(Field.F_RESULT_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_SCOUNT));
        tar.getBizDtl().put(Field.F_MSG_DIR, ExtractTag.extract(tar.getPayload(), Tag.T_MSG_DIR));
        tar.getBizDtl().put(Field.F_MSG_TYPE, ExtractTag.extract(tar.getPayload(), Tag.T_MESSAGETYPE));

        // ADS
        tar.getBizDtl().put(Field.F_ADVERTISING_MARKETING_PROVIDER_ID, ExtractTag.extract(tar.getPayload(), Tag.T_AMPID));
        tar.getBizDtl().put(Field.F_ADVERTISING_MARKETING_MODULE_IMPRESSION_ID, ExtractTag.extract(tar.getPayload(), Tag.T_AMMIID));
        tar.getBizDtl().put(Field.F_ADVERTISING_MARKETING_MODULE_TRACKING_DATA, ExtractTag.extract(tar.getPayload(), Tag.T_AMDATA));
    }

}