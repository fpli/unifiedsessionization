package com.ebay.epic.business.normalizer.global.event.ubi.domain;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.EventPrimaryAsset;
import com.ebay.epic.common.model.avro.GlobalEvent;
import com.ebay.epic.common.model.avro.SojEvent;
import com.ebay.epic.utils.ExtractTag;
import com.ebay.sojourner.common.sojlib.SOJExtractFlag;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.ebay.epic.utils.MapPutIf.putIfNotBlankStr;
import static com.ebay.epic.utils.MapPutIf.putIfNumericStr;

public class A2CNormalizer extends AcceptorNormalizer<SojEvent, GlobalEvent> {
    private final static int CODE_MOBILE_A2C = 1;

    @Override
    public int accept(SojEvent src, GlobalEvent tar) {
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
    public void update(int code, SojEvent src, GlobalEvent tar) {
        tar.setEventPrimaryAsset(EventPrimaryAsset.SHOPPING_CART_PAGE);

        // Item
        if (Objects.isNull(tar.getItemId())) try {
            String extract = ExtractTag.extract(tar.getPayload(), Tag.T_ITM);
            if (StringUtils.isNoneEmpty(extract)) {
                tar.setItemId(Long.parseLong(extract));
            }
        } catch (NumberFormatException ignored) {
        }

        putIfNumericStr(tar.getBizDtl(), Field.F_META_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_META));
        putIfNumericStr(tar.getBizDtl(), Field.F_L1_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L1));
        putIfNumericStr(tar.getBizDtl(), Field.F_L2_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L2));
        putIfNumericStr(tar.getBizDtl(), Field.F_LEAF_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_LEAF));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_ITEM_TITLE, ExtractTag.extract(tar.getPayload(), Tag.T_ITM_TITLE));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_TIME_REMAINING, ExtractTag.extract(tar.getPayload(), Tag.T_TR));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_WATCHERS, ExtractTag.extract(tar.getPayload(), Tag.T_NW));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CURRENT_PRICE, ExtractTag.extract(tar.getPayload(), Tag.T_CUR_PRICE));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_SALE_TYPE, ExtractTag.extract(tar.getPayload(), Tag.T_ST));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_AVAILABLE_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QTYA));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_SOLD_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QTYS));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_VARIATION_ID, ExtractTag.extract(tar.getPayload(), Tag.T_VAR));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_VARIATION_SOLD_OUT_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_VAR_SOLD_OUT));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_SELLER_FEEDBACK_PERCENT, ExtractTag.extract(tar.getPayload(), Tag.T_FDP));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_ITEM_CONDITION, ExtractTag.extract(tar.getPayload(), Tag.T_ITM_COND));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_VI_REVIEW_AVG_RATING, ExtractTag.extract(tar.getPayload(), Tag.T_VIRVW_AVG));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_VI_REVIEW_TOTAL_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_VIRVW_CNT));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_SHIP_SITE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_SHIP_SITE_ID));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_PROD_REF_ID, ExtractTag.extract(tar.getPayload(), Tag.T_PRI));

        // BIN
        putIfNotBlankStr(tar.getBizDtl(), Field.F_BIN_AMT, ExtractTag.extract(tar.getPayload(), Tag.T_BIN_AMT));

        // A2C
        putIfNotBlankStr(tar.getBizDtl(), Field.F_EBAY_CART_ID, ExtractTag.extract(tar.getPayload(), Tag.T_EBC));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_ADD_TO_CART_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_ADD_TO_CART));
//      putIfNotBlankStr(tar.getBizDtl(), F_BUYABLE_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_B_IC));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CART_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_CART_IC));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_COMMAND, ExtractTag.extract(tar.getPayload(), Tag.T_CMD));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CART_SIZE, ExtractTag.extract(tar.getPayload(), Tag.T_CRT_SZ));
//      putIfNotBlankStr(tar.getBizDtl(), F_MULTI_CURRENCY_CART_FLG, ExtractTag.extract(tar.getPayload(), T_MCC));
//      putIfNotBlankStr(tar.getBizDtl(), F_MULTI_QUANTITY_ITEM_NUM, ExtractTag.extract(tar.getPayload(), T_NMQ));
//      putIfNotBlankStr(tar.getBizDtl(), F_UNAVAILABLE_ITEM_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_ITM_UNAVBL));
//      putIfNotBlankStr(tar.getBizDtl(), F_TRANSACTION_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_T_IC));
//      putIfNotBlankStr(tar.getBizDtl(), F_UNBUYABLE_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_U_IC));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CART_GMV, ExtractTag.extract(tar.getPayload(), Tag.T_CART_GMV));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CART_CURRENCY, ExtractTag.extract(tar.getPayload(), Tag.T_CRT_CUR));
//      putIfNotBlankStr(tar.getBizDtl(), F_CART_UNIQUE_SELLERS, ExtractTag.extract(tar.getPayload(), T_CRT_SLR));
//      putIfNotBlankStr(tar.getBizDtl(), F_CART_BUCKET_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_BUCKETS));
//      putIfNotBlankStr(tar.getBizDtl(), F_SELLER_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_SLR));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CART_SUB_TOTAL, ExtractTag.extract(tar.getPayload(), Tag.T_CART_SUB_TOT));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_SHIPPING_SUB_TOTAL, ExtractTag.extract(tar.getPayload(), Tag.T_SHIP_SUB_TOT));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_ITEM_ADDED_TO_CART, ExtractTag.extract(tar.getPayload(), Tag.T_CRT_IA));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_REFERRAL_PAGE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_RPG));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_ACTION_NAME, ExtractTag.extract(tar.getPayload(), Tag.T_AN));

        putIfNotBlankStr(tar.getBizDtl(), Field.F_PRICE, ExtractTag.extract(tar.getPayload(), Tag.T_PRICE));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QUAN));
//      putIfNotBlankStr(tar.getBizDtl(), F_SAVE_FOR_LATER_ITEM_NUM, ExtractTag.extract(tar.getPayload(), T_NUM_SFL_ITM));
//      putIfNotBlankStr(tar.getBizDtl(), F_SAVE_FOR_LATER_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_S_IC));
//      putIfNotBlankStr(tar.getBizDtl(), F_CART_URGENCY_MSGS, ExtractTag.extract(tar.getPayload(), T_CRT_URG));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CART_ID, ExtractTag.extract(tar.getPayload(), Tag.T_CART_ID));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CART_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_CART_QTY));
    }

}