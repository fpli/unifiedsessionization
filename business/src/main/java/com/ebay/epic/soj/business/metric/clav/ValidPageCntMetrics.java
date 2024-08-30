package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

import java.util.Arrays;
import java.util.List;

public class ValidPageCntMetrics extends ClavSessionFieldMetrics {

    private static final List<Integer> list = Arrays.asList(2557882, 2691, 2491192, 2364992, 3289, 2550030, 2056976, 2486706);

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        if (uniEvent.isClavValidPage() && !list.contains(uniEvent.getPageId())) {
            clavSession.setValidPageCount(clavSession.getValidPageCount() + 1);
        }
    }
}
