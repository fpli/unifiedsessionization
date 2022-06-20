package com.ebay.epic.business.filter;

import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.utils.Property;
import com.ebay.epic.utils.SOJSampleHash;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

import static com.ebay.epic.utils.FlinkEnvUtils.getFloat;
import static com.ebay.epic.utils.FlinkEnvUtils.getStringArray;

public class EmptyGuidFilter extends CombinationFilter<RawEvent> {
    @Override
    public boolean filter(RawEvent rawEvent) throws Exception {
        return rawEvent.getGuid() != null;
    }
}
