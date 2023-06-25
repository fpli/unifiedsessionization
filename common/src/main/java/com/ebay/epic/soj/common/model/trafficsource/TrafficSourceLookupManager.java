package com.ebay.epic.soj.common.model.trafficsource;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TrafficSourceLookupManager {
    private static TrafficSourceLookupManager trafficSourceLookupManager =
            new TrafficSourceLookupManager();
    private Map<Long, DwMpxRotation> dwMpxRotationMap =
            Collections.unmodifiableMap(new HashMap<>());
    private Map<Integer, Page> pageMap =
            Collections.unmodifiableMap(new HashMap<>());

    private TrafficSourceLookupManager() {
        loadPages();
        loadRotations();
    }

    private void loadPages() {
        Map<Integer, Page> pages = new HashMap<>();
        try {
            long start = System.currentTimeMillis();
            Schema schema = new Schema.Parser().parse(
                    TrafficSourceLookupManager.class.getResourceAsStream(
                            "/trafficsource/page.avsc"));
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
            InputStream in = TrafficSourceLookupManager.class.getResourceAsStream(
                    "/trafficsource/lkp_pages.avro");
            DataFileStream<GenericRecord> dataFileStream = new DataFileStream<>(in, datumReader);
            GenericRecord page = null;
            while (dataFileStream.hasNext()) {
                // Reuse object by passing it to next(). This saves us from allocating and garbage
                // collecting many objects for files with many items.
                page = dataFileStream.next(page);
                Integer pageId = (Integer) page.get("page_id");
                String pageName = (String) page.get("page_name").toString();
                Integer iframe = (Integer) page.get("iframe");
                pages.put(pageId, new Page(pageId, pageName, iframe));
            }
            long end = System.currentTimeMillis();
            log.info("Duration to load pages: " + ((end - start)) + " (ms)");
            log.info("Pages loaded: " + pages.size());
        } catch (Exception e) {
            log.error("failed to load pages", e);
        }
        pageMap = Collections.unmodifiableMap(pages);
    }

    private void loadRotations() {
        Map<Long, DwMpxRotation> rotations = new HashMap<>();
        try {
            long start = System.currentTimeMillis();
            Schema schema = new Schema.Parser().parse(
                    TrafficSourceLookupManager.class.getResourceAsStream(
                            "/trafficsource/rotation.avsc"));
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
            InputStream in = TrafficSourceLookupManager.class.getResourceAsStream(
                    "/trafficsource/lkp_rotations.avro");
            DataFileStream<GenericRecord> dataFileStream = new DataFileStream<>(in, datumReader);
            GenericRecord rotation = null;
            while (dataFileStream.hasNext()) {
                // Reuse object by passing it to next(). This saves us from allocating and garbage
                // collecting many objects for files with many items.
                rotation = dataFileStream.next(rotation);
                Long rotationId = (Long) rotation.get("rotation_id");
                Integer mpxChnlId = (Integer) rotation.get("mpx_chnl_id");
                rotations.put(rotationId, new DwMpxRotation(rotationId, mpxChnlId));
            }
            long end = System.currentTimeMillis();
            log.info("Duration (ms) to load rotations: " + ((end - start)) + " (ms)");
            log.info("Rotations loaded: " + rotations.size());
        } catch (Exception e) {
            log.error("failed to load rotations", e);
        }
        dwMpxRotationMap = Collections.unmodifiableMap(rotations);
    }

    public static TrafficSourceLookupManager getInstance() {
        return trafficSourceLookupManager;
    }

    public Map<Long, DwMpxRotation> getDwMpxRotationMap() {
        return dwMpxRotationMap;
    }

    public Map<Integer, Page> getPageMap() {
        return pageMap;
    }
}
