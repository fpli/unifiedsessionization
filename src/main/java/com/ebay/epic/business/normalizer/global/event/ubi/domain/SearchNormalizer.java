package com.ebay.epic.business.normalizer.global.event.ubi.domain;

import com.ebay.epic.business.normalizer.ubi.AcceptorNormalizer;
import com.ebay.epic.common.constant.EventPrimaryAsset;
import com.ebay.epic.common.model.avro.GlobalEvent;
import com.ebay.epic.common.model.avro.SojEvent;
import com.ebay.epic.utils.ExtractTag;
import com.ebay.epic.utils.SojUtils;
import com.ebay.sojourner.common.sojlib.SOJExtractFlag;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import static com.ebay.epic.business.constant.ubi.domain.Field.*;
import static com.ebay.epic.business.constant.ubi.domain.Tag.*;
import static com.ebay.epic.utils.MapPutIf.putIfNotBlankStr;

public final class SearchNormalizer extends AcceptorNormalizer<SojEvent, GlobalEvent> {
    private final static int CODE_ADVANCED_SEARCH = 1;
    private final static int CODE_COMPLETED_SEARCH = 2;
    private final static int CODE_SIMILAR_SEARCH = 3;
    private final static int CODE_MOBILE_SEARCH = 4;

    @Override
    public int accept(SojEvent src, GlobalEvent tar) {
        if (ImmutableSet.of(3286, 2045573).contains(tar.getPageId()) && SOJExtractFlag.extractFlag(src.getFlags(), 267) > 0) {
            return CODE_COMPLETED_SEARCH;
        }
        if (ImmutableSet.of(3286, 2045573, 2056193, 2380866).contains(tar.getPageId())) {
            if (StringUtils.contains(ExtractTag.extract(tar.getPayload(), T_GF), "LH_Complete")
                    || StringUtils.contains(ExtractTag.extract(tar.getPayload(), T_GF), "LH_Sold")
                    || StringUtils.contains(ExtractTag.extract(tar.getPayload(), T_GF), "CompletedItems")
                    || StringUtils.contains(ExtractTag.extract(tar.getPayload(), T_GF), "SoldItems")) {
                return CODE_COMPLETED_SEARCH;
            }
            return CODE_DEFAULT_ACCEPT;
        }
        if (ImmutableSet.of(2047936, 2053742, 2059706).contains(tar.getPageId())) {
            return CODE_MOBILE_SEARCH;
        }
        if (ImmutableSet.of(2351460).contains(tar.getPageId()) && StringUtils.equalsIgnoreCase(ExtractTag.extract(tar.getPayload(), T_EACTN), "EXPC")) {
            return CODE_MOBILE_SEARCH;
        }
        if (ImmutableSet.of(2381081).contains(tar.getPageId()) && StringUtils.equalsIgnoreCase(ExtractTag.extract(tar.getPayload(), T_EACTN), "EXPM")) {
            return CODE_MOBILE_SEARCH;
        }
        if (ImmutableSet.of(1468757, 1677950).contains(tar.getPageId())) {
            return CODE_MOBILE_SEARCH;
        }

        return super.accept(src, tar);
    }

    @Override
    public void update(int code, SojEvent src, GlobalEvent tar) {
        tar.setEventPrimaryAsset(EventPrimaryAsset.SEARCH_RESULT_PAGE);
        switch (code) {
            case CODE_ADVANCED_SEARCH:
//                tar.setSubPageType(SubPageType.advanced_search.toString());
                break;
            case CODE_COMPLETED_SEARCH:
//                tar.setSubPageType(SubPageType.completed_search.toString());
                break;
            case CODE_SIMILAR_SEARCH:
//                tar.setSubPageType(SubPageType.similar_search.toString());
                break;
            default:
//                tar.setSubPageType(SubPageType.other.toString());
        }

        // Search
        putIfNotBlankStr(tar.getBizDtl(), F_GLOBAL_FILTER, ExtractTag.extract(tar.getPayload(), T_GF));
        putIfNotBlankStr(tar.getBizDtl(), F_CASSINI_REQUEST_ID, ExtractTag.extract(tar.getPayload(), T_C_RID));
        putIfNotBlankStr(tar.getBizDtl(), F_CURRENT_PAGE_NUM, ExtractTag.extract(tar.getPayload(), T_CPNIP));
        putIfNotBlankStr(tar.getBizDtl(), F_ITEM_CONFIGURED_PER_PAGE, ExtractTag.extract(tar.getPayload(), T_ICPP));
        putIfNotBlankStr(tar.getBizDtl(), F_RESULT_SET_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), T_SHIT));
        putIfNotBlankStr(tar.getBizDtl(), F_CASSINI_QUERY_COST, ExtractTag.extract(tar.getPayload(), T_CQC));
        putIfNotBlankStr(tar.getBizDtl(), F_APPLIED_ASPECT_NUM, ExtractTag.extract(tar.getPayload(), T_NAA));
        putIfNotBlankStr(tar.getBizDtl(), F_APPLIED_ASPECT, ExtractTag.extract(tar.getPayload(), T_AA));
        putIfNotBlankStr(tar.getBizDtl(), F_SORT, ExtractTag.extract(tar.getPayload(), T_SORT));
        putIfNotBlankStr(tar.getBizDtl(), F_ITEM_LIST, SojUtils.base36dDecode(ExtractTag.extract(tar.getPayload(), T_ITM)));
        putIfNotBlankStr(tar.getBizDtl(), F_CONDITION_FILTER_SURFACED_FLG, ExtractTag.extract(tar.getPayload(), T_S_COND));
        putIfNotBlankStr(tar.getBizDtl(), F_CONDITION_FILTER_APPLIED_FLG, ExtractTag.extract(tar.getPayload(), T_A_COND));
        putIfNotBlankStr(tar.getBizDtl(), F_ACTION_NAME, ExtractTag.extract(tar.getPayload(), T_AN));

        putIfNotBlankStr(tar.getBizDtl(), F_LEAF_FIRST_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), T_LF_CAT));
        putIfNotBlankStr(tar.getBizDtl(), F_TOP_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), T_T_CAT_ID));
        putIfNotBlankStr(tar.getBizDtl(), F_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), T_CAT));
        putIfNotBlankStr(tar.getBizDtl(), F_CATEGORY_LVL, ExtractTag.extract(tar.getPayload(), T_CAT_LVL));

        // BrowseNode
        putIfNotBlankStr(tar.getBizDtl(), F_BROWSE_NODE_ID, ExtractTag.extract(tar.getPayload(), T_BN));
    }

}
