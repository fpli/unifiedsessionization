package com.ebay.epic.soj.common.dds;

import com.ebay.platform.dds.api.RestClient;
import com.ebay.platform.dds.model.RestRequest;
import com.ebay.platform.dds.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Slf4j
public class DdsRestClient implements RestClient {

    private final String targetDomain;
    private final String consumer;

    private OkHttpClient httpClient;

    public DdsRestClient(DdsConfig ddsConfig) {
        targetDomain = ddsConfig.getTargetDomain();
        consumer = ddsConfig.getConsumer();
        init(ddsConfig);
    }

    @Override
    public RestResponse<InputStream> requestData(RestRequest request) {
        String path = request.getBasePath() + requestDataPath;
        long clientLastModified = request.getLastModified();
        String checksum = request.getChecksum();

        try {
            val httpRequest = generateRequest(path, clientLastModified, checksum);

            val response = httpClient.newCall(httpRequest).execute();
            val code = response.code();

            if (code == HttpStatus.SC_OK && nonNull(response.body())) {
                return new RestResponse<>(code, response.body().byteStream(), getHeaderMap(response));
            }

            if (code == HttpStatus.SC_NOT_MODIFIED) {
                log.info("The data in dds svc has no change, the checksum still equals {}", request.getChecksum());
                return new RestResponse<>(code, null, Collections.emptyMap());
            }

            log.error("Unable to get dds data from dds server. Status: {}", code);
            return new RestResponse<>(code, null, Collections.emptyMap());
        } catch (Exception e) {
            log.error("Unable to get dds data from dds server. Client({})", this.getClass().getSimpleName(), e);
        }
        return null;
    }

    private void init(DdsConfig ddsConfig) {
        val httpBuilder = new OkHttpClient().newBuilder();

        httpBuilder.connectTimeout(ddsConfig.getRestTimeoutMs(), TimeUnit.MILLISECONDS);
        httpBuilder.callTimeout(ddsConfig.getRestTimeoutMs(), TimeUnit.MILLISECONDS);
        httpBuilder.writeTimeout(ddsConfig.getRestTimeoutMs(), TimeUnit.MILLISECONDS);
        httpBuilder.readTimeout(ddsConfig.getRestTimeoutMs(), TimeUnit.MILLISECONDS);

        val dispatcher = new Dispatcher();

        dispatcher.setMaxRequests(ddsConfig.getRestMaxRequests());
        dispatcher.setMaxRequestsPerHost(ddsConfig.getRestMaxRequests());
        httpBuilder.dispatcher(dispatcher);

        httpBuilder.connectionPool(new ConnectionPool(ddsConfig.getRestMaxIdleConnections(), 1, TimeUnit.MINUTES));

        log.info("Initialized OkHttpClient");
        httpClient = httpBuilder.build();
    }

    private Request generateRequest(String path, long clientLastModified, String checksum) {
        val requestBuilder = new Request.Builder();
        requestBuilder.get()
                      .url(targetDomain + path)
                      .addHeader("Authorization", consumer)
                      .addHeader("Checksum", formatChecksum(checksum))
                      .addHeader("accept-encoding", "deflate")
                      .addHeader("DataVersion", "v2");

        if (clientLastModified > 0) {
            Date lastModified = new Date(clientLastModified);
            DateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            String lastModifiedTimestamp = dateFormatter.format(lastModified);
            requestBuilder.addHeader("Last-Modified", lastModifiedTimestamp)
                          .addHeader("If-Modified-Since", lastModifiedTimestamp);
        }

        return requestBuilder.build();
    }

    private Map<String, String> getHeaderMap(Response response) {
        Headers headers = response.headers();

        Map<String, String> headerMap = new HashMap<>();
        for (String key : headers.names()) {
            val values = headers.values(key);
            if (isNotEmpty(values)) {
                headerMap.put(key, headers.values(key).get(0));
            }
        }
        return headerMap;
    }

    private String formatChecksum(String checksum) {
        return ofNullable(checksum).orElse("empty");
    }
}
