package com.ebay.epic.soj.common.dds;

import com.ebay.platform.dds.api.DeviceApiProvider;
import com.ebay.platform.dds.impl.DeviceApiProviderImpl;
import com.ebay.platform.raptor.dds.impl.DeviceAtlasV2ApiFactory;
import com.ebay.platform.raptor.dds.impl.DeviceAtlasV3ApiFactory;
import com.ebay.platform.raptor.ddsmodels.DeviceInfo;
import com.ebay.platform.raptor.raptordds.parsers.UserAgentParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;


@Slf4j
public class UAParserFactory {

    private static volatile UserAgentParser userAgentParser;

    public static UserAgentParser getInstance(String targetDomain) throws IOException {
        DdsConfig ddsConfig = new DdsConfig(
            targetDomain,
            "unifiedsession-rt",
            "V3",
            30000,
            600000,
            120000,
            100,
            10
        );


        if (isNull(userAgentParser)) {
            synchronized (UAParserFactory.class) {
                if (isNull(userAgentParser)) {
                    userAgentParser = new UserAgentParser(init(ddsConfig), null);
                }
            }
        }
        return userAgentParser;
    }

    private static DeviceApiProvider<DeviceInfo> init(DdsConfig ddsConfig) throws IOException {
        val ddsLocation = new File("/tmp/dds");
        if (!ddsLocation.exists() && !ddsLocation.mkdir()) {
            throw new IOException("Failed to create dds location");
        }

        val ddsRestClient = new DdsRestClient(ddsConfig);
        val deviceAtlasV2ApiFactory = new DeviceAtlasV2ApiFactory(ddsLocation, ddsRestClient);
        val deviceAtlasV3ApiFactory = new DeviceAtlasV3ApiFactory(ddsLocation, ddsRestClient);
        val deviceApiProvider = new DeviceApiProviderImpl<>(ddsConfig, deviceAtlasV2ApiFactory, deviceAtlasV3ApiFactory);
        val startTime = currentTimeMillis();
        log.info("Start to init device info manager");
        deviceApiProvider.start();
        log.info("Finish to init device info manager, cost {} ms", currentTimeMillis() - startTime);
        return deviceApiProvider;
    }
}
