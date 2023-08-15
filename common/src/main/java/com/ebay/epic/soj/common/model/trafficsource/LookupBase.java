package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class LookupBase<K, V> {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    public static final String LKP_HDFS_BASE =
            "viewfs://apollo-rno/sys/soj/ubd/traffic-source/lookup";
    public static final String LKP_CLASSPATH_BASE = "/trafficsource";

    protected LoadStatus loadStatus;
    protected LookupSource lookupSource;
    protected String location;
    protected long timestamp;
    protected Map<K, V> kvMap;

    public boolean initialize() {
        log.info("Start to initialize lookup");
        log.info("Load from HDFS first");
        if (loadFromHdfs() == LoadStatus.SUCCESS) {
            return true;
        }

        log.info("Fall back to static file on classpath");
        if (loadFromClasspath() == LoadStatus.SUCCESS) {
            return true;
        }

        log.warn("Initialization failed");
        return false;
    }

    public boolean outOfDate() {
        long lastModificationTime = getHdfsFileModificationTime();
        if (lastModificationTime > timestamp) {
            log.info("kvMap is out-of-date. Need to refresh. (current: {}, latest: {})",
                    timestamp, lastModificationTime);
            return true;
        } else {
            log.info("kvMap is update-to-date. Don't need refresh. (current: {}, latest: {})",
                    timestamp, lastModificationTime);
            return false;
        }
    }

    public long getHdfsFileModificationTime() {
        try {
            FileSystem fileSystem = getFileSystem();
            String file = dataHdfsLocation();
            long modificationTime = fileSystem.listStatus(new Path(file))[0].getModificationTime();
            log.info("{} modification time: {}", file, modificationTime);
            return modificationTime;
        } catch (Exception e) {
            log.warn("Failed to check HDFS file modification time", e);
            return 0;
        }
    }

    public LoadStatus loadFromHdfs() {
        log.info("Try to load kvMap from HDFS");
        try {
            FileSystem fileSystem = getFileSystem();
            String file = dataHdfsLocation();
            Path path = new Path(file);
            long modificationTime = fileSystem.listStatus(path)[0].getModificationTime();
            kvMap = loadFromInputStream(fileSystem.open(path));
            loadStatus = LoadStatus.SUCCESS;
            lookupSource = LookupSource.HDFS;
            location = file;
            timestamp = modificationTime;
            log.info("Load kvMap from HDFS successfully. {}", this);
            return LoadStatus.SUCCESS;
        } catch (Exception e) {
            log.warn("Failed to load kvMap from HDFS", e);
        }
        return LoadStatus.FAIL;
    }

    private FileSystem getFileSystem() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setBoolean("fs.hdfs.impl.disable.cache", true);
        return FileSystem.newInstance(configuration);
    }

    public LoadStatus loadFromClasspath() {
        log.info("Try to load kvMap from classpath");
        try {
            String dataClasspathLocation = dataClasspathLocation();
            InputStream in = LookupBase.class.getResourceAsStream(dataClasspathLocation);
            kvMap = loadFromInputStream(in);
            loadStatus = LoadStatus.SUCCESS;
            lookupSource = LookupSource.CLASSPATH;
            location = dataClasspathLocation;
            timestamp = 0;
            log.info("Load kvMap from classpath successfully. ({})", this);
            return LoadStatus.SUCCESS;
        } catch (Exception e) {
            log.warn("Failed to load kvMap", e);
        }
        return LoadStatus.FAIL;
    }

    private Map<K, V> loadFromInputStream(InputStream inputStream)
            throws Exception {
        Map<K, V> kvMap = new HashMap<>();
        long start = System.currentTimeMillis();
        Schema schema = new Schema.Parser().parse(schemaString());
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        DataFileStream<GenericRecord> dataFileStream =
                new DataFileStream<>(inputStream, datumReader);
        GenericRecord genericRecord = null;
        while (dataFileStream.hasNext()) {
            // Reuse object by passing it to next(). This saves us from allocating and garbage
            // collecting many objects for files with many items.
            genericRecord = dataFileStream.next(genericRecord);
            Map.Entry<K, V> entry = extractMapEntry(genericRecord);
            kvMap.put(entry.getKey(), entry.getValue());
        }
        long end = System.currentTimeMillis();
        log.info("Duration (ms) to load kvMap: " + ((end - start)) + " (ms)");
        log.info("Entries in kvMap loaded: " + kvMap.size());
        return Collections.unmodifiableMap(kvMap);
    }

    protected abstract String schemaString();

    public String toString() {
        return new ToStringBuilder(this)
                .append("loadStatus", loadStatus)
                .append("lookupSource", lookupSource)
                .append("location", location)
                .append("timestamp", timestamp)
                .append("entries", kvMap.size())
                .build();
    }

    protected abstract String dataHdfsLocation();

    protected abstract String dataClasspathLocation();

    protected abstract Map.Entry<K, V> extractMapEntry(GenericRecord genericRecord);
}
