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
import com.google.common.collect.ImmutableSet;

public class BrowseNodeNormalizer extends AcceptorNormalizer<SojEvent, UniTrackingEvent> {

    @Override
    public int accept(SojEvent src, UniTrackingEvent tar) {
        return acceptBoolean(ImmutableSet.of(2489527).contains(tar.getPageId()));
    }

    @Override
    public void update(int code, SojEvent src, UniTrackingEvent tar) {
        tar.setPageType(PageType.BROWSE_NODE);
        tar.setSubPageType(SubPageType.OTHER);

        // Search
        tar.getBizDtl().put(Field.F_GLOBAL_FILTER, ExtractTag.extract(tar.getPayload(), Tag.T_GF));
        tar.getBizDtl().put(Field.F_CASSINI_REQUEST_ID, ExtractTag.extract(tar.getPayload(), Tag.T_C_RID));
        tar.getBizDtl().put(Field.F_CURRENT_PAGE_NUM, ExtractTag.extract(tar.getPayload(), Tag.T_CPNIP));
        tar.getBizDtl().put(Field.F_ITEM_CONFIGURED_PER_PAGE, ExtractTag.extract(tar.getPayload(), Tag.T_ICPP));
        tar.getBizDtl().put(Field.F_RESULT_SET_ITEM_COUNT, ExtractTag.extract(tar.getPayload(), Tag.T_SHIT));
        tar.getBizDtl().put(Field.F_CASSINI_QUERY_COST, ExtractTag.extract(tar.getPayload(), Tag.T_CQC));
        tar.getBizDtl().put(Field.F_APPLIED_ASPECT_NUM, ExtractTag.extract(tar.getPayload(), Tag.T_NAA));
        tar.getBizDtl().put(Field.F_ITEM_LIST, SojUtils.base36dDecode(ExtractTag.extract(tar.getPayload(), Tag.T_ITM)));
        tar.getBizDtl().put(Field.F_ITEM_VARIATION_LIST, SojUtils.base36dDecode(ExtractTag.extract(tar.getPayload(), Tag.T_VAR_ITM)));
        tar.getBizDtl().put(Field.F_CONDITION_FILTER_SURFACED_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_S_COND));
        tar.getBizDtl().put(Field.F_CONDITION_FILTER_APPLIED_FLG, ExtractTag.extract(tar.getPayload(), Tag.T_A_COND));
        tar.getBizDtl().put(Field.F_ACTION_NAME, ExtractTag.extract(tar.getPayload(), Tag.T_AN));

        tar.getBizDtl().put(Field.F_LEAF_FIRST_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_LF_CAT));
        tar.getBizDtl().put(Field.F_TOP_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_T_CAT_ID));
        tar.getBizDtl().put(Field.F_CATEGORY_ID, ExtractTag.extract(tar.getPayload(), Tag.T_CAT));
        tar.getBizDtl().put(Field.F_CATEGORY_LVL, ExtractTag.extract(tar.getPayload(), Tag.T_CAT_LVL));

        // BrowseNode
        tar.getBizDtl().put(Field.F_BROWSE_NODE_ID, ExtractTag.extract(tar.getPayload(), Tag.T_BN));
        tar.getBizDtl().put(Field.F_BROWSE_PAGE_TYPE, ExtractTag.extract(tar.getPayload(), Tag.T_P_TYPE));
    }

}