package com.ebay.epic.business.normalizer.ubi.domain;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.PageType;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.constant.SubPageType;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.utils.ExtractTag;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class OfferNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
        return acceptBoolean(ImmutableSet.of(2545344, 2050464, 2062351, 2494951, 2495273, 2495274, 2494950, 2494956, 2494958, 2494953, 2484108, 2510396, 3733487, 3727692, 3733485, 3733482, 3733484).contains(tar.getPageId()));
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.OFFER);
        tar.setPageType(SubPageType.OTHER);

        // Item
        tar.getBizDtl().put(Field.F_META_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_META));
        tar.getBizDtl().put(Field.F_L1_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L1));
        tar.getBizDtl().put(Field.F_L2_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L2));
        tar.getBizDtl().put(Field.F_LEAF_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_LEAF));
        tar.getBizDtl().put(Field.F_ITEM_TITLE, ExtractTag.extract(tar.getPayload(), Tag.T_ITM_TITLE));
        tar.getBizDtl().put(Field.F_TIME_REMAINING, ExtractTag.extract(tar.getPayload(), Tag.T_TR));
        tar.getBizDtl().put(Field.F_CURRENT_PRICE, ExtractTag.extract(tar.getPayload(), Tag.T_CUR_PRICE));
        tar.getBizDtl().put(Field.F_SHIP_SITE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_SHIP_SITE_ID));
        if (Objects.isNull(tar.getItemId())) try {
            String extract = ExtractTag.extract(tar.getPayload(), Tag.T_ITM);
            if (StringUtils.isNoneEmpty(extract)) {
                tar.setItemId(Long.parseLong(extract));
            }
        } catch (NumberFormatException ignored) {
        }

        // Bid
        tar.getBizDtl().put(Field.F_BID_AMT, ExtractTag.extract(tar.getPayload(), Tag.T_BID_AMT));

        // BIN
        tar.getBizDtl().put(Field.F_BIN_AMT, ExtractTag.extract(tar.getPayload(), Tag.T_BIN_AMT));

        // Offer
        tar.getBizDtl().put(Field.F_TYPE, ExtractTag.extract(tar.getPayload(), Tag.T_TYPE));
        tar.getBizDtl().put(Field.F_BUYING_FLOW_OPERATION, ExtractTag.extract(tar.getPayload(), Tag.T_BFLOWOP));
        tar.getBizDtl().put(Field.F_C2C_SELLER_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_SLRC2C));
        tar.getBizDtl().put(Field.F_PRICE_GUIDANCE_QUALIFIED, ExtractTag.extract(tar.getPayload(), Tag.T_PGQ));
        tar.getBizDtl().put(Field.F_PRICE_GUIDANCE_AVAILABLE, ExtractTag.extract(tar.getPayload(), Tag.T_PGA));
        tar.getBizDtl().put(Field.F_ACTION_NAME, ExtractTag.extract(tar.getPayload(), Tag.T_AN));
        tar.getBizDtl().put(Field.F_AUTO_DECLINE_PRICE, ExtractTag.extract(tar.getPayload(), Tag.T_AUTO_DCL));
        tar.getBizDtl().put(Field.F_THREAD_ID, ExtractTag.extract(tar.getPayload(), Tag.T_THID));
        tar.getBizDtl().put(Field.F_OFFER_ID, ExtractTag.extract(tar.getPayload(), Tag.T_OFID));
        tar.getBizDtl().put(Field.F_CURRENCY, ExtractTag.extract(tar.getPayload(), Tag.T_CRNCY));
        tar.getBizDtl().put(Field.F_FEEDBACK, ExtractTag.extract(tar.getPayload(), Tag.T_FEEDBACK));
        tar.getBizDtl().put(Field.F_MESSAGE, ExtractTag.extract(tar.getPayload(), Tag.T_MESSAGE));
        tar.getBizDtl().put(Field.F_AFTER_PAY_OFFER_ID, ExtractTag.extract(tar.getPayload(), Tag.T_AP_OFF_ID));
    }

}