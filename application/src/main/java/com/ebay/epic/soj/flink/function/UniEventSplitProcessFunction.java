package com.ebay.epic.soj.flink.function;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.SlidingWindowReservoir;
import com.ebay.epic.common.constant.Constants;
import com.ebay.epic.common.constant.OutputTagConstants;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.UniEvent;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardHistogramWrapper;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.flink.util.Preconditions;

import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UniEventSplitProcessFunction extends ProcessFunction<UniEvent,UniEvent> {

    private Map<String,OutputTag<UniEvent>> outputTagMap;
    private static final String siteToSource = "site_to_source";
    private static final String siteToSink = "site_to_sink";
    private static final String sourceToSink = "source_to_sink";
    private static final String sojournerUpstreamDelay = "unified_sess_upstream_delay";
    private static final String sojournerKafkaFetchDelay = "unified_sess_kafka_fetch_delay";
    private final int latencyWindowSize;
    private transient DropwizardHistogramWrapper siteToSourceSWWrapper;
    private transient DropwizardHistogramWrapper siteToSourceSNWrapper;
    private transient DropwizardHistogramWrapper siteToSourceSBWrapper;
    private transient DropwizardHistogramWrapper siteToSourceSNBWrapper;
    private transient DropwizardHistogramWrapper siteToSourceUNBWrapper;
    private transient DropwizardHistogramWrapper siteToSinkSWWrapper;
    private transient DropwizardHistogramWrapper siteToSinkSNWrapper;
    private transient DropwizardHistogramWrapper siteToSinkSBWrapper;
    private transient DropwizardHistogramWrapper siteToSinkSNBWrapper;
    private transient DropwizardHistogramWrapper siteToSinkUNBWrapper;
    private transient DropwizardHistogramWrapper sourceToSinkWrapper;
    private transient DropwizardHistogramWrapper USUpstreamDelaySWWrapper;
    private transient DropwizardHistogramWrapper USUpstreamDelaySNWrapper;
    private transient DropwizardHistogramWrapper USUpstreamDelaySBWrapper;
    private transient DropwizardHistogramWrapper USUpstreamDelaySNBWrapper;
    private transient DropwizardHistogramWrapper USUpstreamDelayUNBWrapper;

    private transient DropwizardHistogramWrapper USKafkaFetchDelayWrapper;
    private transient Map<EventType,DropwizardHistogramWrapper> s2sourceDhwMap;
    private transient Map<EventType,DropwizardHistogramWrapper> s2sinkDhwMap;
    private transient Map<EventType,DropwizardHistogramWrapper> upstreamDhwMap;
    private boolean enableMetrics = false;
    public UniEventSplitProcessFunction(EventType eventType,int latencyWindowSize, boolean enableMetrics){
        if(eventType.getName().equals(EventType.DEFAULT_LATE.getName()))
        {
            outputTagMap=OutputTagConstants.outputTagMapLate;
        }else{
            outputTagMap=OutputTagConstants.outputTagMapMain;
        }
        this.latencyWindowSize = latencyWindowSize;
        this.enableMetrics=enableMetrics;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        if(enableMetrics) {
            siteToSourceSWWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SURFACE_WEB_METRICS_GROUP)
                    .histogram(siteToSource, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSourceSNWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SURFACE_NATIVE_METRICS_GROUP)
                    .histogram(siteToSource, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSourceSBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SOJ_BOT_METRICS_GROUP)
                    .histogram(siteToSource, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSourceSNBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SOJ_NONBOT_METRICS_GROUP)
                    .histogram(siteToSource, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSourceUNBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.UTP_NONBOT_METRICS_GROUP)
                    .histogram(siteToSource, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            s2sourceDhwMap = new ConcurrentHashMap<>();
            s2sourceDhwMap.put(EventType.AUTOTRACK_WEB,siteToSourceSWWrapper);
            s2sourceDhwMap.put(EventType.AUTOTRACK_NATIVE,siteToSourceSNWrapper);
            s2sourceDhwMap.put(EventType.UBI_BOT,siteToSourceSBWrapper);
            s2sourceDhwMap.put(EventType.UBI_NONBOT,siteToSourceSNBWrapper);
            s2sourceDhwMap.put(EventType.UTP_NONBOT,siteToSourceUNBWrapper);

            siteToSinkSWWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SURFACE_WEB_METRICS_GROUP)
                    .histogram(siteToSink, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSinkSNWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SURFACE_NATIVE_METRICS_GROUP)
                    .histogram(siteToSink, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSinkSBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SOJ_BOT_METRICS_GROUP)
                    .histogram(siteToSink, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSinkSNBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SOJ_NONBOT_METRICS_GROUP)
                    .histogram(siteToSink, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            siteToSinkUNBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.UTP_NONBOT_METRICS_GROUP)
                    .histogram(siteToSink, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            s2sinkDhwMap= new ConcurrentHashMap<>();
            s2sinkDhwMap.put(EventType.AUTOTRACK_WEB,siteToSinkSWWrapper);
            s2sinkDhwMap.put(EventType.AUTOTRACK_NATIVE,siteToSinkSNWrapper);
            s2sinkDhwMap.put(EventType.UBI_BOT,siteToSinkSBWrapper);
            s2sinkDhwMap.put(EventType.UBI_NONBOT,siteToSinkSNBWrapper);
            s2sinkDhwMap.put(EventType.UTP_NONBOT,siteToSinkUNBWrapper);

            sourceToSinkWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP)
                    .histogram(sourceToSink, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));

            USUpstreamDelaySWWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SURFACE_WEB_METRICS_GROUP)
                    .histogram(sojournerUpstreamDelay, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            USUpstreamDelaySNWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SURFACE_NATIVE_METRICS_GROUP)
                    .histogram(sojournerUpstreamDelay, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            USUpstreamDelaySBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SOJ_BOT_METRICS_GROUP)
                    .histogram(sojournerUpstreamDelay, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            USUpstreamDelaySNBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.SOJ_NONBOT_METRICS_GROUP)
                    .histogram(sojournerUpstreamDelay, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
            USUpstreamDelayUNBWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP,Constants.UTP_NONBOT_METRICS_GROUP)
                    .histogram(sojournerUpstreamDelay, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));

            upstreamDhwMap= new ConcurrentHashMap<>();
            upstreamDhwMap.put(EventType.AUTOTRACK_WEB,USUpstreamDelaySWWrapper);
            upstreamDhwMap.put(EventType.AUTOTRACK_NATIVE,USUpstreamDelaySNWrapper);
            upstreamDhwMap.put(EventType.UBI_BOT,USUpstreamDelaySBWrapper);
            upstreamDhwMap.put(EventType.UBI_NONBOT,USUpstreamDelaySNBWrapper);
            upstreamDhwMap.put(EventType.UTP_NONBOT,USUpstreamDelayUNBWrapper);

            USKafkaFetchDelayWrapper = getRuntimeContext().getMetricGroup()
                    .addGroup(Constants.UNIFIED_SESSION_METRICS_GROUP)
                    .histogram(sojournerKafkaFetchDelay, new DropwizardHistogramWrapper(new Histogram(
                            new SlidingWindowReservoir(latencyWindowSize))));
        }
    }
    @Override
    public void processElement(UniEvent value, Context context, Collector<UniEvent> out) {
        Preconditions.checkNotNull(outputTagMap);
        context.output(outputTagMap.get(value.getEventType().getFullName()),value);
        if(enableMetrics) {
            long end = new Date().getTime();
            long eventTs = value.getEventTs();
            long siteToSource = value.getIngestTimestamp() - eventTs;
            long siteToSink = end - eventTs;
            long sourceToSink = (end - value.getIngestTimestamp());
            long sojournerUpstreamDelayMs = value.getKafkaReceivedTimestamp() - eventTs;
            long sojournerKafkaFetchDelayMs = value.getIngestTimestamp() - value.getKafkaReceivedTimestamp();
            upstreamDhwMap.get(value.getEventType()).update(sojournerUpstreamDelayMs);
            USKafkaFetchDelayWrapper.update(sojournerKafkaFetchDelayMs);
            s2sourceDhwMap.get(value.getEventType()).update(siteToSource);
            s2sinkDhwMap.get(value.getEventType()).update(siteToSink);
            sourceToSinkWrapper.update(sourceToSink);
        }

        out.collect(null);
    }
}
