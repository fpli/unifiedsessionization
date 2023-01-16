package com.ebay.epic.flink.connector.kafka;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.flink.connector.kafka.config.ConfigManager;
import com.ebay.epic.flink.connector.kafka.config.FlinkKafkaSourceConfigWrapper;
import com.ebay.epic.flink.connector.kafka.config.KafkaConsumerConfig;
import com.ebay.epic.flink.connector.kafka.factory.FlinkKafkaConsumerFactory;
import com.ebay.epic.utils.Property;
import com.google.common.base.Preconditions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;

import static com.ebay.epic.utils.FlinkEnvUtils.getInteger;
import static com.ebay.epic.utils.FlinkEnvUtils.getString;

public class SourceDataStreamBuilder<T> {

    private final StreamExecutionEnvironment environment;
    private DataCenter dc;
    private String operatorName;
    private String uid;
    private String slotGroup;
    private int parallelism = getInteger(Property.DEFAULT_PARALLELISM);
    private int maxParallelism = getInteger(Property.DEFAULT_MAX_PARALLELISM);
    private int outOfOrderlessInMin;
    private String fromTimestamp = "0";
    private int idleSourceTimeout;
    private boolean rescaled;
    private EventType eventType;
    private ConfigManager configManager;
    public SourceDataStreamBuilder(StreamExecutionEnvironment environment,DataCenter dc,EventType eventType) {
        this.environment = environment;
        this.dc=dc;
        this.eventType=eventType;
        this.configManager= new ConfigManager(dc,eventType,true);
    }

    public SourceDataStreamBuilder<T> operatorName(String operatorName) {
        this.operatorName = configManager.getOPName(operatorName);
        return this;
    }

    public SourceDataStreamBuilder<T> parallelism(String parallelism) {
        this.parallelism = configManager.getParallelism(parallelism);
        return this;
    }

    public SourceDataStreamBuilder<T> uid(String uid) {
        this.uid = configManager.getOPUid(uid);
        return this;
    }

    public SourceDataStreamBuilder<T> slotGroup(String slotGroup) {
        this.slotGroup = configManager.getSlotSharingGroup(slotGroup);
        return this;
    }

    public SourceDataStreamBuilder<T> rescaled(boolean rescaled) {
        this.rescaled = rescaled;
        return this;
    }

    public SourceDataStreamBuilder<T> outOfOrderlessInMin(String outOfOrderlessInMin) {
        this.outOfOrderlessInMin = configManager.getIntValueNODC(outOfOrderlessInMin);
        return this;
    }

    public SourceDataStreamBuilder<T> fromTimestamp(String fromTimestamp) {
        this.fromTimestamp = configManager.getStrValueNODC(fromTimestamp);
        return this;
    }

    public SourceDataStreamBuilder<T> idleSourceTimeout(String idleSourceTimeout) {
        this.idleSourceTimeout = configManager.getIntValueNODC(idleSourceTimeout);
        return this;
    }

    public DataStream<T> build(KafkaDeserializationSchema<T> schema) {
        Preconditions.checkNotNull(dc);
        return this.build(schema, dc, operatorName, parallelism, uid, slotGroup, rescaled);
    }

    public DataStream<T> buildRescaled(KafkaDeserializationSchema<T> schema) {
        Preconditions.checkNotNull(dc);
        return this.build(schema, dc, operatorName, parallelism, uid, slotGroup, true);
    }

    public DataStream<T> build(KafkaDeserializationSchema<T> schema, DataCenter dc,
                               String operatorName, int parallelism, String uid, String slotGroup,
                               boolean rescaled) {
        Preconditions.checkNotNull(dc);
        KafkaConsumerConfig config = KafkaConsumerConfig.build(dc,this.eventType);
        FlinkKafkaSourceConfigWrapper configWrapper = new FlinkKafkaSourceConfigWrapper(
                config, outOfOrderlessInMin, idleSourceTimeout, fromTimestamp);
        FlinkKafkaConsumerFactory factory = new FlinkKafkaConsumerFactory(configWrapper);

        DataStream<T> dataStream = environment
                .addSource(factory.get(schema))
                .setParallelism(parallelism)
                .slotSharingGroup(slotGroup)
                .name(operatorName)
                .uid(uid)
                .setMaxParallelism(maxParallelism);

        if (rescaled) {
            return dataStream.rescale();
        }

        return dataStream;
    }
}
