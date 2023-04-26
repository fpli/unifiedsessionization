package com.ebay.epic.soj.common.state;

import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.contrib.streaming.state.PredefinedOptions;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;

@Slf4j
public class StateBackendFactory {

    public static final String FS = "FS";
    public static final String ROCKSDB = "ROCKSDB";
    public static final String CHECKPOINT_DATA_URI =
            "file://" + FlinkEnvUtils.getString(Property.CHECKPOINT_DATA_DIR);

    public static StateBackend getStateBackend(String type) {
        switch (type) {
            case FS:
                return new FsStateBackend(CHECKPOINT_DATA_URI);
            case ROCKSDB:
                try {
                    RocksDBStateBackend rocksDBStateBackend =
                            new RocksDBStateBackend(CHECKPOINT_DATA_URI, true);
                    rocksDBStateBackend.setPredefinedOptions(PredefinedOptions.FLASH_SSD_OPTIMIZED);
                    return rocksDBStateBackend;
                } catch (Exception e) {
                    log.error("Failed to create RocksDB state backend", e);
                    throw new RuntimeException(e);
                }
            default:
                throw new RuntimeException("Unknown state backend type");
        }
    }
}
