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

public class ViewItemNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {
    private final static int CODE_MOBILE_VIEW_ITEM = 1;

    /**
     * TODO @Edward: Load 3rd party look up table
     * select split(value, '\t')[0] page_id, split(value, '\t')[4] page_type
     * from text.`/user/b_bis/config/soj_lkp_page_iframe/latest/`
     * where split(value, '\t')[4] = 'VI'
     */
    private transient ImmutableSet<Integer> soj_lkp_page;

    @Override
    public void init() throws Exception {
        super.init();
        soj_lkp_page = ImmutableSet.of(2034086, 1698351, 2042596, 1468719, 4979, 2052027, 1679850, 2052049, 1702526, 1636305, 2043178, 1468726, 284, 1698355, 2052039, 2051117, 1677971, 5924, 2045314, 4340, 1679190, 2041537, 2047935, 4954, 2053846, 2919, 1468768, 1468725, 2047675, 1468728, 1702712, 2034043, 1673582, 1468764, 2034046, 1664345, 1468729, 1702525, 2034062, 2047465, 1702724, 2041531, 1468766, 1468769, 1696540, 2034050, 2051116, 1696528, 2054436, 1468767, 2053317, 1468727, 2052060, 2052054, 1679178, 1664344, 5408, 4069, 2052070, 5938, 1702527, 5015, 2034061, 1677718, 1625135, 1698526, 1468765, 1696501, 2041864, 1700429);
    }

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
//when event.page_id in (284, 4340, 4979, 2047675, 5924, 1881, 2919, 4069, 4954, 2052197 , 2054897, 2060182) then 'VI'
        if (ImmutableSet.of(284, 4340, 4979, 2047675, 5924, 1881, 2919, 4069, 4954, 2052197, 2054897, 2060182).contains(tar.getPageId())) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2047935, 2052268, 5408, 2349624, 1468725, 1468726, 1468766, 1677971, 2034043, 2034061, 2041531, 2047465, 2047935) then 'MobileVI'
        if (ImmutableSet.of(2047935, 2052268, 5408, 2349624, 1468725, 1468726, 1468766, 1677971, 2034043, 2034061, 2041531, 2047465, 2047935).contains(tar.getPageId())) {
            return CODE_MOBILE_VIEW_ITEM;
        }
//when event.page_id in (2056116) and coalesce(sojlib.soj_extract_nvp(payload, 'pfn', '&', '='), sojlib.soj_extract_nvp(payload, '!pfn', '&', '='), sojlib.soj_extract_nvp(payload, '!_pfn', '&', '=')) = 'VI' then 'MobileVI'
        if (ImmutableSet.of(2056116).contains(tar.getPageId()) && (tar.getPayload().getOrDefault("pfn", "").equalsIgnoreCase("VI") || tar.getPayload().getOrDefault("!pfn", "").equalsIgnoreCase("VI") || tar.getPayload().getOrDefault("!_pfn", "").equalsIgnoreCase("VI"))) {
            return CODE_MOBILE_VIEW_ITEM;
        }
//when event.page_id in (1468719, 1673582) then 'MobileVI'
        if (ImmutableSet.of(1468719, 1673582).contains(tar.getPageId())) {
            return CODE_MOBILE_VIEW_ITEM;
        }
//when lkp.page_id is not null then 'VI'
        if (soj_lkp_page.contains(tar.getPageId())) {
            return CODE_DEFAULT_ACCEPT;
        }
//when event.page_id in (2056016, 2058306, 2349624) then 'VI_LAYER_PAGE_IDS'
        if (ImmutableSet.of(2056016, 2058306, 2349624).contains(tar.getPageId())) {
            return CODE_DEFAULT_ACCEPT;
        }

        return super.accept(src, tar);
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.VIEW_ITEM);
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
        tar.getBizDtl().put(Field.F_CURRENT_PRICE_USD, ExtractTag.extract(tar.getPayload(), Tag.T_CP_USD));
        tar.getBizDtl().put(Field.F_SALE_TYPE, ExtractTag.extract(tar.getPayload(), Tag.T_ST));
        tar.getBizDtl().put(Field.F_AVAILABLE_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QTYA));
        tar.getBizDtl().put(Field.F_SOLD_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_QTYS));
        tar.getBizDtl().put(Field.F_REMAINING_QUANTITY, ExtractTag.extract(tar.getPayload(), Tag.T_VI_QTY_REMAIN));
        tar.getBizDtl().put(Field.F_VARIATION_ID, ExtractTag.extract(tar.getPayload(), Tag.T_VAR));
        tar.getBizDtl().put(Field.F_VARIATION_SOLD_OUT_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_VAR_SOLD_OUT));
        tar.getBizDtl().put(Field.F_SELLER_FEEDBACK_PERCENT, ExtractTag.extract(tar.getPayload(), Tag.T_FDP));
        tar.getBizDtl().put(Field.F_SELLER_FEEDBACK_SCORE, ExtractTag.extract(tar.getPayload(), Tag.T_FB_SCORE));
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

        // BIN
        tar.getBizDtl().put(Field.F_BIN_AMT, ExtractTag.extract(tar.getPayload(), Tag.T_BIN_AMT));

        // PL
        tar.getBizDtl().put(Field.F_PROMOTED_LISTING_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_PROMO_L));
    }

}