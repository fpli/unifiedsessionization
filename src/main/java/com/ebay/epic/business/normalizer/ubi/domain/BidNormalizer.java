package com.ebay.epic.business.normalizer.ubi.domain;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.PageType;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.constant.SubPageType;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.utils.ExtractTag;
import com.ebay.sojourner.common.sojlib.SOJExtractFlag;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class BidNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {
    private final static int CODE_MOBILE_BID = 1;

    /**
     * TODO @Edward: Load 3rd party look up table
     * select split(value, '\t')[0] page_id, split(value, '\t')[4] page_type
     * from text.`/user/b_bis/config/soj_lkp_page_iframe/latest/`
     * where split(value, '\t')[4] = 'BID'
     */
    private transient ImmutableSet<Integer> soj_lkp_page;

    @Override
    public void init() throws Exception {
        super.init();
        soj_lkp_page = ImmutableSet.of(2045328, 1468716, 2045329, 5414, 0, 2050510, 2052202, 4068, 2050509, 2050447, 2055470, 1468718, 167, 1468714, 2045327, 2045326, 2052178, 2046740, 2053249);
    }

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
//when event.page_id in (167, 5414, 4068) and soj_extract_flag(flags, 3) and rdt = 0 then 'BID'
        if (ImmutableSet.of(167, 5414, 4068).contains(tar.getPageId()) && SOJExtractFlag.extractFlag(src.getFlags(), 3) > 0 && src.getRdt() == 0) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2486514, 2486515) and rdt = 0 then 'BID'
        if (ImmutableSet.of(2486514, 2486515).contains(tar.getPageId()) && src.getRdt() == 0) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (3897) and soj_extract_flag(flags, 59) then 'BID'
        if (ImmutableSet.of(3897).contains(tar.getPageId()) && SOJExtractFlag.extractFlag(src.getFlags(), 59) > 0) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2050447, 2050509, 2056815, 2056814, 2171217, 2057885) then 'MobileBID'
        if (ImmutableSet.of(2050447, 2050509, 2056815, 2056814, 2171217, 2057885).contains(tar.getPageId())) {
            return CODE_MOBILE_BID;
        }
//when event.page_id in (2048308, 5374, 1468716, 2041937, 1677915)
//	and lower(coalesce(sojlib.soj_extract_nvp(payload, 'type', '&', '='), sojlib.soj_extract_nvp(payload, '!type', '&', '='), sojlib.soj_extract_nvp(payload, '!_type', '&', '='))) = 'bid' then 'MobileBID'
        if (ImmutableSet.of(2048308, 5374, 1468716, 2041937, 1677915).contains(tar.getPageId()) && StringUtils.equalsIgnoreCase(ExtractTag.extract(tar.getPayload(), Tag.T_TYPE), "bid")) {
            return CODE_MOBILE_BID;
        }
//when lkp.page_id is not null then 'BID'
        if (soj_lkp_page.contains(tar.getPageId())) {
            return CODE_DEFAULT_ACCEPT;
        }

        return super.accept(src, tar);
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.BID);
        tar.setSubPageType(SubPageType.OTHER);

        // Item
        tar.getBizDtl().put(Field.F_META_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_META));
        tar.getBizDtl().put(Field.F_L1_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L1));
        tar.getBizDtl().put(Field.F_L2_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_L2));
        tar.getBizDtl().put(Field.F_LEAF_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_LEAF));
        tar.getBizDtl().put(Field.F_ITEM_TITLE, ExtractTag.extract(tar.getPayload(), Tag.T_ITM_TITLE));
        tar.getBizDtl().put(Field.F_TIME_REMAINING, ExtractTag.extract(tar.getPayload(), Tag.T_TR));
        tar.getBizDtl().put(Field.F_WATCHERS, ExtractTag.extract(tar.getPayload(), Tag.T_NW));
        tar.getBizDtl().put(Field.F_BIDDERS, ExtractTag.extract(tar.getPayload(), Tag.T_BDRS));
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
        if (Objects.isNull(tar.getItemId())) try {
            String extract = ExtractTag.extract(tar.getPayload(), Tag.T_ITM);
            if (StringUtils.isNoneEmpty(extract)) {
                tar.setItemId(Long.parseLong(extract));
            }
        } catch (NumberFormatException ignored) {
        }
        tar.getBizDtl().put(Field.F_SHIP_SITE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_SHIP_SITE_ID));
        tar.getBizDtl().put(Field.F_PROD_REF_ID, ExtractTag.extract(tar.getPayload(), Tag.T_PRI));
        tar.getBizDtl().put(Field.F_BID_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_BC));

        // Bid
        tar.getBizDtl().put(Field.F_BID_TRANSACTION_ID, ExtractTag.extract(tar.getPayload(), Tag.T_BTI));

        // BIN
        tar.getBizDtl().put(Field.F_BIN_AMT, ExtractTag.extract(tar.getPayload(), Tag.T_BIN_AMT));
    }

}