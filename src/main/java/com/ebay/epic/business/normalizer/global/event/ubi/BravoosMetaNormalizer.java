package com.ebay.epic.business.normalizer.global.event.ubi;

import com.ebay.epic.business.normalizer.FieldNormalizer;
import com.ebay.epic.common.model.avro.GlobalEvent;
import com.ebay.epic.common.model.avro.SojEvent;
import com.ebay.epic.utils.BravoosMetaService;
import com.ebay.epic.utils.CommonUtils;
import com.ebay.epic.utils.EventsCommonUtils;
import lombok.val;

public class BravoosMetaNormalizer extends FieldNormalizer<SojEvent, GlobalEvent> {

    private BravoosMetaService lookup;

    @Override
    public void init() throws Exception {
        super.init();
        lookup = BravoosMetaService.getInstance();
    }

    @Override
    public void normalize(SojEvent src, GlobalEvent tar) throws Exception {

        if (tar.getPageId() != null) {
            val pageMetadata = lookup.getPageMeta(tar.getPageId());
            if (pageMetadata != null) {
                tar.setPageName(pageMetadata.getPageName());
                tar.setPageGroupId(pageMetadata.getPageGroupId());
                tar.setPageGroupName(pageMetadata.getPageGroupName());
                tar.setPageGroupDesc(pageMetadata.getPageGroupDesc());
            }
        }

        val moduleIdStr = EventsCommonUtils.getModuleId(tar);
        if (CommonUtils.isMeaningFullString(moduleIdStr)) {
            //Module
            val moduleId = CommonUtils.safeParseInteger(moduleIdStr);
            tar.setModuleId(moduleId);

            val moduleMetadata = lookup.getModuleMeta(moduleId);
            if (moduleMetadata != null) {
                tar.setElementName(moduleMetadata.getElementName());
                tar.setModuleName(moduleMetadata.getModuleName());
                tar.setModuleDesc(moduleMetadata.getModuleDesc());
            }
        }

        val clickIdStr = EventsCommonUtils.getClickId(tar);
        if (CommonUtils.isMeaningFullString(clickIdStr)) {
            //Click
            Integer clickId = CommonUtils.safeParseInteger(clickIdStr);
            tar.setClickId(clickId);

            val clickMeta = lookup.getClickMeta(clickId);
            if (clickMeta != null) {
                tar.setClickName(clickMeta.getClickName());
                tar.setClickDesc(clickMeta.getClickDesc());
            }
        }

    }

    @Override
    public void close() throws Exception {
        lookup.close();
        lookup = null;
        super.close();
    }

}
