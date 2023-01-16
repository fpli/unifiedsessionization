package com.ebay.epic.flink.connector.kafka;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.flink.connector.kafka.config.ConfigManager;
import com.ebay.epic.utils.Property;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import static com.ebay.epic.utils.FlinkEnvUtils.getInteger;
import static com.ebay.epic.utils.Property.DEFAULT_PARALLELISM;

public class FlinkFilterFunctionBuilder<T> {

    private final DataStream<T> dataStream;
    private DataCenter dc;
    private String operatorName;
    private String uid;
    private String slotGroup;
    private int parallelism = getInteger(DEFAULT_PARALLELISM);
    private int maxParallelism = getInteger(Property.DEFAULT_MAX_PARALLELISM);
    private EventType eventType;
    private ConfigManager configManager;
    private RichFilterFunction<T> richFilterFunction;

    public FlinkFilterFunctionBuilder(DataStream<T> dataStream, DataCenter dc, Boolean isDrived) {
        this(dataStream, dc, EventType.DEFAULT, isDrived);
    }

    public FlinkFilterFunctionBuilder(DataStream<T> dataStream, DataCenter dc, EventType eventType, Boolean isDrived) {
        this.dataStream = dataStream;
        this.dc = dc;
        this.eventType = eventType;
        this.configManager = new ConfigManager();
        this.configManager.setEventType(eventType);
        this.configManager.setDrived(isDrived);
    }


    public FlinkFilterFunctionBuilder<T> operatorName(String operatorName) {
        this.operatorName = configManager.getOPName(operatorName);
        return this;
    }

    public FlinkFilterFunctionBuilder<T> parallelism(String parallelism) {
        this.parallelism = configManager.getParallelism(parallelism);
        return this;
    }

    public FlinkFilterFunctionBuilder<T> uid(String uid) {
        this.uid = configManager.getOPUid(uid);
        return this;
    }

    public FlinkFilterFunctionBuilder<T> slotGroup(String slotGroup) {
        this.slotGroup = configManager.getStrDirect(slotGroup);
        return this;
    }

    public FlinkFilterFunctionBuilder<T> filter(RichFilterFunction<T> richFilterFunction) {
        this.richFilterFunction = richFilterFunction;
        return this;
    }

    public SingleOutputStreamOperator<T> build() {
        return dataStream.filter(richFilterFunction)
                .setParallelism(parallelism)
                .name(operatorName)
                .slotSharingGroup(slotGroup)
                .uid(uid)
                .setMaxParallelism(maxParallelism);
    }
}
