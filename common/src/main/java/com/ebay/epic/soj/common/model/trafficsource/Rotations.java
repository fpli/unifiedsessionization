package com.ebay.epic.soj.common.model.trafficsource;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;

@Slf4j
public class Rotations extends LookupBase<Long, DwMpxRotation> {
    private static final String DATA_FILENAME = "lkp_rotations.avro";
    private static final String SCHEMA_FILENAME = "rotation.avsc";

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
    protected Map.Entry<Long, DwMpxRotation> extractMapEntry(GenericRecord genericRecord) {
        Long rotationId = (Long) genericRecord.get("rotation_id");
        Integer mpxChnlId = (Integer) genericRecord.get("mpx_chnl_id");
        return new AbstractMap.SimpleEntry(
                rotationId,
                new DwMpxRotation(rotationId, mpxChnlId));
    }
}
