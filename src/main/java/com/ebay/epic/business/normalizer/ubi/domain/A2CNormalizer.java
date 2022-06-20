package com.ebay.epic.business.normalizer.ubi.domain;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.PageType;
import com.ebay.epic.common.constant.SubPageType;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.utils.ExtractTag;
import com.ebay.sojourner.common.sojlib.SOJExtractFlag;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class A2CNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {
    private final static int CODE_MOBILE_A2C = 1;

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
//when event.page_id in (4930, 2493971)
//	and coalesce(sojlib.soj_extract_nvp(payload, 'crtia', '&', '='), sojlib.soj_extract_nvp(payload, '!crtia', '&', '='), sojlib.soj_extract_nvp(payload, '!_crtia', '&', '=')) is not null then 'Add2Cart'
        if (ImmutableSet.of(4930, 2493971).contains(tar.getPageId()) && StringUtils.isNoneEmpty(ExtractTag.extract(tar.getPayload(), Tag.T_CRT_IA))) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2484178)
//	and coalesce(sojlib.soj_extract_nvp(payload, 'cart_id', '&', '='), sojlib.soj_extract_nvp(payload, '!cart_id', '&', '='), sojlib.soj_extract_nvp(payload, '!_cart_id', '&', '=')) is not null
//	and coalesce(sojlib.soj_extract_nvp(payload, 'item', '&', '='), sojlib.soj_extract_nvp(payload, '!item', '&', '='), sojlib.soj_extract_nvp(payload, '!_item', '&', '=')) is not null then 'Add2Cart'
        if (ImmutableSet.of(2484178).contains(tar.getPageId()) && StringUtils.isNoneEmpty(ExtractTag.extract(tar.getPayload(), Tag.T_CART_ID)) && StringUtils.isNoneEmpty(ExtractTag.extract(tar.getPayload(), "item"))) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2046448) and soj_extract_flag(flags, 1) /*Make sure it's adding an item rather than removing*/ then 'Add2Cart'
        if (ImmutableSet.of(2046448).contains(tar.getPageId()) && SOJExtractFlag.extractFlag(src.getFlags(), 1) > 0) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2053918, 2053922, 2057192, 2058919, 2053897, 2051686, 2332490, 2485302) then 'MobileAdd2Cart'
        if (ImmutableSet.of(2053918, 2053922, 2057192, 2058919, 2053897, 2051686, 2332490, 2485302).contains(tar.getPageId())) {
            return CODE_MOBILE_A2C;
        }

        return super.accept(src, tar);
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.ADD_TO_CART);

        tar.setSubPageType(SubPageType.OTHER);

        // Item
        tar.getBizDtl().put(Field.F_META_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_META));
        tar.getBizDtl().put(Field.F_L1_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L1));
        tar.getBizDtl().put(Field.F_L2_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L2));
        tar.getBizDtl().put(Field.F_LEAF_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_LEAF));
        if (Objects.isNull(tar.getItemId())) try {
            String extract = ExtractTag.extract(tar.getPayload(), Tag.T_ITM);
            if (StringUtils.isNoneEmpty(extract)) {
                tar.setItemId(Long.parseLong(extract));
            }
        } catch (NumberFormatException ignored) {
        }
        tar.getBizDtl().put(Field.F_ITEM_TITLE, ExtractTag.extract(tar.getPayload(), Tag.T_ITM_TITLE));
        tar.getBizDtl().put(Field.F_TIME_REMAINING, ExtractTag.extract(tar.getPayload(), Tag.T_TR));
        tar.getBizDtl().put(Field.F_WATCHERS, ExtractTag.extract(tar.getPayload(), Tag.T_NW));
        tar.getBizDtl().put(Field.F_CURRENT_PRICE, ExtractTag.extract(tar.getPayload(), Tag.T_CUR_PRICE));
        tar.getBizDtl().put(Field.F_SALE_TYPE, ExtractTag.extract(tar.getPayload(), Tag.T_ST));
        tar.getBizDtl().put(Field.F_AVAILABLE_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QTYA));
        tar.getBizDtl().put(Field.F_SOLD_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QTYS));
        tar.getBizDtl().put(Field.F_VARIATION_ID, ExtractTag.extract(tar.getPayload(), Tag.T_VAR));
        tar.getBizDtl().put(Field.F_VARIATION_SOLD_OUT_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_VAR_SOLD_OUT));
        tar.getBizDtl().put(Field.F_SELLER_FEEDBACK_PERCENT, ExtractTag.extract(tar.getPayload(), Tag.T_FDP));
        tar.getBizDtl().put(Field.F_ITEM_CONDITION, ExtractTag.extract(tar.getPayload(), Tag.T_ITM_COND));
        tar.getBizDtl().put(Field.F_VI_REVIEW_AVG_RATING, ExtractTag.extract(tar.getPayload(), Tag.T_VIRVW_AVG));
        tar.getBizDtl().put(Field.F_VI_REVIEW_TOTAL_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_VIRVW_CNT));
        tar.getBizDtl().put(Field.F_SHIP_SITE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_SHIP_SITE_ID));
        tar.getBizDtl().put(Field.F_PROD_REF_ID, ExtractTag.extract(tar.getPayload(), Tag.T_PRI));

        // BIN
        tar.getBizDtl().put(Field.F_BIN_AMT, ExtractTag.extract(tar.getPayload(), Tag.T_BIN_AMT));

        // A2C
        tar.getBizDtl().put(Field.F_EBAY_CART_ID, ExtractTag.extract(tar.getPayload(), Tag.T_EBC));
        tar.getBizDtl().put(Field.F_ADD_TO_CART_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_ADD_TO_CART));
//      tar.getBizDtl().put(F_BUYABLE_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_B_IC));
        tar.getBizDtl().put(Field.F_CART_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_CART_IC));
        tar.getBizDtl().put(Field.F_COMMAND, ExtractTag.extract(tar.getPayload(), Tag.T_CMD));
        tar.getBizDtl().put(Field.F_CART_SIZE, ExtractTag.extract(tar.getPayload(), Tag.T_CRT_SZ));
//      tar.getBizDtl().put(F_MULTI_CURRENCY_CART_FLG, ExtractTag.extract(tar.getPayload(), T_MCC));
//      tar.getBizDtl().put(F_MULTI_QUANTITY_ITEM_NUM, ExtractTag.extract(tar.getPayload(), T_NMQ));
//      tar.getBizDtl().put(F_UNAVAILABLE_ITEM_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_ITM_UNAVBL));
//      tar.getBizDtl().put(F_TRANSACTION_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_T_IC));
//      tar.getBizDtl().put(F_UNBUYABLE_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_U_IC));
        tar.getBizDtl().put(Field.F_CART_GMV, ExtractTag.extract(tar.getPayload(), Tag.T_CART_GMV));
        tar.getBizDtl().put(Field.F_CART_CURRENCY, ExtractTag.extract(tar.getPayload(), Tag.T_CRT_CUR));
//      tar.getBizDtl().put(F_CART_UNIQUE_SELLERS, ExtractTag.extract(tar.getPayload(), T_CRT_SLR));
//      tar.getBizDtl().put(F_CART_BUCKET_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_BUCKETS));
//      tar.getBizDtl().put(F_SELLER_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_SLR));
        tar.getBizDtl().put(Field.F_CART_SUB_TOTAL, ExtractTag.extract(tar.getPayload(), Tag.T_CART_SUB_TOT));
        tar.getBizDtl().put(Field.F_SHIPPING_SUB_TOTAL, ExtractTag.extract(tar.getPayload(), Tag.T_SHIP_SUB_TOT));
        tar.getBizDtl().put(Field.F_ITEM_ADDED_TO_CART, ExtractTag.extract(tar.getPayload(), Tag.T_CRT_IA));
        tar.getBizDtl().put(Field.F_REFERRAL_PAGE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_RPG));
        tar.getBizDtl().put(Field.F_ACTION_NAME, ExtractTag.extract(tar.getPayload(), Tag.T_AN));

        tar.getBizDtl().put(Field.F_PRICE, ExtractTag.extract(tar.getPayload(), Tag.T_PRICE));
        tar.getBizDtl().put(Field.F_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QUAN));
//      tar.getBizDtl().put(F_SAVE_FOR_LATER_ITEM_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_SFL_ITM));
//      tar.getBizDtl().put(F_SAVE_FOR_LATER_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_S_IC));
//      tar.getBizDtl().put(F_CART_URGENCY_MSGS, ExtractTag.extract(tar.getPayload(), T_CRT_URG));
        tar.getBizDtl().put(Field.F_CART_ID, ExtractTag.extract(tar.getPayload(), Tag.T_CART_ID));
        tar.getBizDtl().put(Field.F_CART_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_CART_QTY));
    }

}