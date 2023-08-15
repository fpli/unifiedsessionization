package com.ebay.epic.soj.common.model.trafficsource;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;

@Slf4j
public class Pages extends LookupBase<Integer, Page> {
    private static final String DATA_FILENAME = "lkp_pages.avro";
    private static final String SCHEMA_FILENAME = "page.avsc";

    private static final String SCHEMA_STRING;

    static {
        String schema;
        try {
            schema = IOUtils.toString(
                    LookupBase.class.getResourceAsStream(
                            LKP_CLASSPATH_BASE + "/" + SCHEMA_FILENAME),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Failed to load schema", e);
            schema = null;
        }
        SCHEMA_STRING = schema;
    }

    @Override
    protected String schemaString() {
        return SCHEMA_STRING;
    }

    @Override
    protected String dataHdfsLocation() {
        return LKP_HDFS_BASE + "/" + DATA_FILENAME;
    }

    @Override
    protected String dataClasspathLocation() {
        return LKP_CLASSPATH_BASE + "/" + DATA_FILENAME;
    }

    @Override
    protected Map.Entry<Integer, Page> extractMapEntry(GenericRecord genericRecord) {
        Integer pageId = (Integer) genericRecord.get("page_id");
        String pageName = genericRecord.get("page_name").toString();
        Integer iframe = (Integer) genericRecord.get("iframe");
        return new AbstractMap.SimpleEntry(
                pageId,
                new Page(pageId, pageName, iframe));
    }
}
