package com.ebay.epic.soj.common.dds;

import com.ebay.platform.dds.api.DeviceAtlasConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DdsConfig implements Serializable, DeviceAtlasConfiguration {

    private String targetDomain;
    private String consumer;
    private String apiVersion;
    private long initialDelay;
    private long period;

    // rest configs
    private long restTimeoutMs;
    private int restMaxRequests;
    private int restMaxIdleConnections;

    @Override
    public boolean enableV3Api() {
        return "V3".equalsIgnoreCase(apiVersion);
    }

}
