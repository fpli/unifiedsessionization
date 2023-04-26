package com.ebay.epic.soj.business.normalizer;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.google.common.collect.Sets;
import org.apache.avro.generic.GenericRecord;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SessionIdsNormalizer extends FieldNormalizer<RawEvent, UniEvent> {

    private static List<Integer> pages = Arrays.asList(2547208);
    private static Set<String> utpTags = Sets.newHashSet("rotid", "chn");

    @Override
    public void normalize(RawEvent src, UniEvent tar) throws Exception {
        tar.setSessionId(src.getSessionId());
        tar.setSessionSkey(src.getSessionSkey());
    }
}
