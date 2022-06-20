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

public class WatchNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {
    private final static int CODE_MOBILE_WATCH = 1;

    /**
     * TODO @Edward: Load 3rd party look up table
     * select split(value, '\t')[0] page_id, split(value, '\t')[4] page_type
     * from text.`/user/b_bis/config/soj_lkp_page_iframe/latest/`
     * where split(value, '\t')[4] = 'WTCH'
     */
    private transient ImmutableSet<Integer> soj_lkp_page;

    @Override
    public void init() throws Exception {
        super.init();
        soj_lkp_page = ImmutableSet.of(5372, 2046018, 3663, 5371, 2047937, 2047646, 2046551, 4018, 2045325, 1677972, 573, 2047645);
    }

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
//when event.page_id in (573, 2679, 3663, 6109, 6110, 6111, 3665, 4018, 2058891, 2176696, 2057640) then 'Watch'
        if (ImmutableSet.of(573, 2679, 3663, 6109, 6110, 6111, 3665, 4018, 2058891, 2176696, 2057640).contains(tar.getPageId())) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2047937, 2057896, 2485954, 2047646, 2055415, 2056818, 5371, 5372, 2047645) then 'MobileWatch'
        if (ImmutableSet.of(2047937, 2057896, 2485954, 2047646, 2055415, 2056818, 5371, 5372, 2047645).contains(tar.getPageId())) {
            return CODE_MOBILE_WATCH;
        }
//when event.page_id in (2046551, 2046018, 1677972) then 'MobileWatch'
        if (ImmutableSet.of(2046551, 2046018, 1677972).contains(tar.getPageId())) {
            return CODE_MOBILE_WATCH;
        }
//when lkp.page_id is not null then 'Watch'
        if (soj_lkp_page.contains(tar.getPageId())) {
            return CODE_MOBILE_WATCH;
        }

        return super.accept(src, tar);
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.WATCH);
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
        tar.getBizDtl().put(Field.F_SHIP_SITE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_SHIP_SITE_ID));
        tar.getBizDtl().put(Field.F_PROD_REF_ID, ExtractTag.extract(tar.getPayload(), Tag.T_PRI));
        tar.getBizDtl().put(Field.F_BID_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_BC));

        // Bid
        tar.getBizDtl().put(Field.F_BID_INCREMENT, ExtractTag.extract(tar.getPayload(), Tag.T_BI));

        // BIN
        tar.getBizDtl().put(Field.F_BIN_AMT, ExtractTag.extract(tar.getPayload(), Tag.T_BIN_AMT));

        // WATCH
        tar.getBizDtl().put(Field.F_WATCH_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_WATCH));
    }

}