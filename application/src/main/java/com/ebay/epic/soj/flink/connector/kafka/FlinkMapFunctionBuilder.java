package com.ebay.epic.soj.flink.connector.kafka;

import com.ebay.epic.soj.common.enums.DataCenter;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.flink.connector.kafka.config.ConfigManager;
import com.ebay.epic.soj.common.utils.Property;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import static com.ebay.epic.soj.flink.utils.FlinkEnvUtils.getInteger;
import static com.ebay.epic.soj.common.utils.Property.DEFAULT_PARALLELISM;

public class FlinkMapFunctionBuilder<IN, OUT> {

    private final DataStream<IN> dataStream;
    private DataCenter dc;
    private String operatorName;
    private String uid;
    private String sinkSlotGroup;
    private int parallelism = getInteger(DEFAULT_PARALLELISM);
    private int maxParallelism = getInteger(Property.MAX_PARALLELISM_DEFAULT);
    private EventType eventType;
    private ConfigManager configManager;
    private RichMapFunction<IN, OUT> richFilterFunction;

    public FlinkMapFunctionBuilder(DataStream<IN> dataStream, DataCenter dc, Boolean isDrived) {
        this.dataStream = dataStream;
        this.dc = dc;
        this.configManager = new ConfigManager();
        this.configManager.setDrived(isDrived);
    }

    public FlinkMapFunctionBuilder(DataStream<IN> dataStream, DataCenter dc, EventType eventType, Boolean isDrived) {
        this.dataStream = dataStream;
        this.dc = dc;
        this.eventType = eventType;
        this.configManager = new ConfigManager();
        this.configManager.setEventType(eventType);
        this.configManager.setDrived(isDrived);
    }

    public FlinkMapFunctionBuilder<IN, OUT> operatorName(String operatorName) {
        this.operatorName = configManager.getOPName(operatorName);
        return this;
    }

    public FlinkMapFunctionBuilder<IN, OUT> parallelism(String parallelism) {
        this.parallelism = configManager.getParallelism(parallelism,
                configManager.constructPostFix(configManager.DEL_POINT));
        return this;
    }

    public FlinkMapFunctionBuilder<IN, OUT> uid(String uid) {
        this.uid = configManager.getOPUid(uid);
        return this;
    }

    public FlinkMapFunctionBuilder<IN, OUT> slotGroup(String slotGroup) {
        this.sinkSlotGroup = configManager.getSlotSharingGroup(slotGroup);
        return this;
    }

    public FlinkMapFunctionBuilder<IN, OUT> map(RichMapFunction<IN, OUT> mapFunction) {
        this.richFilterFunction = mapFunction;
        return this;
    }

    public SingleOutputStreamOperator<OUT> build() {
        return dataStream.map(richFilterFunction)
                .setParallelism(parallelism)
                .name(operatorName)
                .uid(uid)
                .setMaxParallelism(maxParallelism);
    }
}
