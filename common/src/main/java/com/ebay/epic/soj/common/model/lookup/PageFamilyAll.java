package com.ebay.epic.soj.common.model.lookup;

import com.ebay.epic.soj.common.model.trafficsource.LookupBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;

@Slf4j
public class PageFamilyAll extends LookupBase<Integer, PageFamilyInfo> {

    private static final String DATA_FILENAME = "lkp_page_family_all.avro";
    private static final String SCHEMA_FILENAME = "page_family_all.avsc";

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
    protected Map.Entry<Integer, PageFamilyInfo> extractMapEntry(GenericRecord genericRecord) {
        Integer pageId = (Integer) genericRecord.get("page_id");
        String pageFamily1 = genericRecord.get("page_family1") == null ? null : genericRecord.get("page_family1").toString();
        String pageFamily2 = genericRecord.get("page_family2") == null ? null : genericRecord.get("page_family2").toString();
        String pageFamily3 = genericRecord.get("page_family3") == null ? null : genericRecord.get("page_family3").toString();
        String pageFamily4 = genericRecord.get("page_family4") == null ? null : genericRecord.get("page_family4").toString();
        Integer iframe = (Integer) genericRecord.get("iframe");
        return new AbstractMap.SimpleEntry(
                pageId,
                new PageFamilyInfo(pageId, pageFamily1, pageFamily2, pageFamily3, pageFamily4, iframe));
    }

}
