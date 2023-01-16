package com.ebay.epic.flink.connector.kafka;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.flink.connector.kafka.config.ConfigManager;
import com.ebay.epic.utils.Property;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import static com.ebay.epic.utils.FlinkEnvUtils.getInteger;
import static com.ebay.epic.utils.Property.DEFAULT_PARALLELISM;

public class FlinkMapFunctionBuilder<IN, OUT> {

    private final DataStream<IN> dataStream;
    private DataCenter dc;
    private String operatorName;
    private String uid;
    private String sinkSlotGroup;
    private int parallelism = getInteger(DEFAULT_PARALLELISM);
    private int maxParallelism = getInteger(Property.DEFAULT_MAX_PARALLELISM);
    private EventType eventType;
    private ConfigManager configManager;
    private RichMapFunction<IN, OUT> richFilterFunction;

    public FlinkMapFunctionBuilder(DataStream<IN> dataStream, DataCenter dc, Boolean isDrived) {
        this.dataStream = dataStream;
        this.dc = dc;
        this.configManager = new ConfigManager();
        this.configManager.setDrived(isDrived);
    }

    public FlinkMapFunctionBuilder<IN, OUT> operatorName(String operatorName) {
        this.operatorName = configManager.getOPName(operatorName);
        return this;
    }

    public FlinkMapFunctionBuilder<IN, OUT> parallelism(String parallelism) {
        this.parallelism = configManager.getParallelism(parallelism);
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
