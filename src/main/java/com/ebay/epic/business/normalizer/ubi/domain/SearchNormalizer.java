package com.ebay.epic.business.normalizer.ubi.domain;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.PageType;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.constant.SubPageType;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.utils.ExtractTag;
import com.ebay.epic.utils.SojUtils;
import com.ebay.sojourner.common.sojlib.SOJExtractFlag;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

public final class SearchNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {
    private final static int CODE_ADVANCED_SEARCH = 1;
    private final static int CODE_COMPLETED_SEARCH = 2;
    private final static int CODE_SIMILAR_SEARCH = 3;
    private final static int CODE_MOBILE_SEARCH = 4;

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
        if (ImmutableSet.of(3286, 2045573).contains(tar.getPageId()) && SOJExtractFlag.extractFlag(src.getFlags(), 267) > 0) {
            return CODE_COMPLETED_SEARCH;
        }
        if (ImmutableSet.of(3286, 2045573, 2056193, 2380866).contains(tar.getPageId())) {
            if (StringUtils.contains(ExtractTag.extract(tar.getPayload(), Tag.T_GF), "LH_Complete")
                    || StringUtils.contains(ExtractTag.extract(tar.getPayload(), Tag.T_GF), "LH_Sold")
                    || StringUtils.contains(ExtractTag.extract(tar.getPayload(), Tag.T_GF), "CompletedItems")
                    || StringUtils.contains(ExtractTag.extract(tar.getPayload(), Tag.T_GF), "SoldItems")) {
                return CODE_COMPLETED_SEARCH;
            }
            return CODE_DEFAULT_ACCEPT;
        }
        if (ImmutableSet.of(2047936, 2053742, 2059706).contains(tar.getPageId())) {
            return CODE_MOBILE_SEARCH;
        }
        if (ImmutableSet.of(2351460).contains(tar.getPageId()) && StringUtils.equalsIgnoreCase(ExtractTag.extract(tar.getPayload(), Tag.T_EACTN), "EXPC")) {
            return CODE_MOBILE_SEARCH;
        }
        if (ImmutableSet.of(2381081).contains(tar.getPageId()) && StringUtils.equalsIgnoreCase(ExtractTag.extract(tar.getPayload(), Tag.T_EACTN), "EXPM")) {
            return CODE_MOBILE_SEARCH;
        }
        if (ImmutableSet.of(1468757, 1677950).contains(tar.getPageId())) {
            return CODE_MOBILE_SEARCH;
        }

        return super.accept(src, tar);
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.SEARCH);
        switch (code) {
            case CODE_ADVANCED_SEARCH:
                tar.setSubPageType(SubPageType.ADVANCED_SEARCH);
                break;
            case CODE_COMPLETED_SEARCH:
                tar.setSubPageType(SubPageType.COMPLETED_SEARCH);
                break;
            case CODE_SIMILAR_SEARCH:
                tar.setSubPageType(SubPageType.SIMILAR_SEARCH);
                break;
            default:
                tar.setSubPageType(SubPageType.OTHER);
        }

        // Search
        tar.getBizDtl().put(Field.F_GLOBAL_FILTER, ExtractTag.extract(tar.getPayload(), Tag.T_GF));
        tar.getBizDtl().put(Field.F_CASSINI_REQUEST_ID, ExtractTag.extract(tar.getPayload(), Tag.T_C_RID));
        tar.getBizDtl().put(Field.F_CURRENT_PAGE_NUM, ExtractTag.extract(tar.getPayload(), Tag.T_CPNIP));
        tar.getBizDtl().put(Field.F_ITEM_CONFIGURED_PER_PAGE, ExtractTag.extract(tar.getPayload(), Tag.T_ICPP));
        tar.getBizDtl().put(Field.F_RESULT_SET_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_SHIT));
        tar.getBizDtl().put(Field.F_CASSINI_QUERY_COST, ExtractTag.extract(tar.getPayload(), Tag.T_CQC));
        tar.getBizDtl().put(Field.F_APPLIED_ASPECT_NUM, ExtractTag.extract(tar.getPayload(), Tag.T_NAA));
        tar.getBizDtl().put(Field.F_APPLIED_ASPECT, ExtractTag.extract(tar.getPayload(), Tag.T_AA));
        tar.getBizDtl().put(Field.F_SORT, ExtractTag.extract(tar.getPayload(), Tag.T_SORT));
        tar.getBizDtl().put(Field.F_ITEM_LIST, SojUtils.base36dDecode(ExtractTag.extract(tar.getPayload(), Tag.T_ITM)));
        tar.getBizDtl().put(Field.F_CONDITION_FILTER_SURFACED_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_S_COND));
        tar.getBizDtl().put(Field.F_CONDITION_FILTER_APPLIED_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_A_COND));
        tar.getBizDtl().put(Field.F_ACTION_NAME, ExtractTag.extract(tar.getPayload(), Tag.T_AN));

        tar.getBizDtl().put(Field.F_LEAF_FIRST_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_LF_CAT));
        tar.getBizDtl().put(Field.F_TOP_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_T_CAT_ID));
        tar.getBizDtl().put(Field.F_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_CAT));
        tar.getBizDtl().put(Field.F_CATEGORY_LVL, ExtractTag.extract(tar.getPayload(), Tag.T_CAT_LVL));

        // BrowseNode
        tar.getBizDtl().put(Field.F_BROWSE_NODE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_BN));
    }

}
