package com.ebay.epic.flink.connector.kafka;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.flink.connector.kafka.config.ConfigManager;
import com.ebay.epic.utils.Property;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;

import static com.ebay.epic.utils.FlinkEnvUtils.getInteger;
import static com.ebay.epic.utils.Property.DEFAULT_PARALLELISM;

public class FlinkSplitFunctionBuilder<T> {

    private final DataStream<T> dataStream;
    private String operatorName;
    private String uid;
    private String slotGroup;
    private int parallelism = getInteger(DEFAULT_PARALLELISM);
    private int maxParallelism = getInteger(Property.MAX_PARALLELISM_SINK);
    private ConfigManager configManager;
    private ProcessFunction<T,T> processFunction;
    private EventType eventType;

    public FlinkSplitFunctionBuilder(DataStream<T> dataStream, EventType eventType, Boolean isDrived) {
        this.dataStream = dataStream;
        this.eventType=eventType;
        this.configManager = new ConfigManager();
        this.configManager.setDrived(isDrived);
        this.configManager.setEventType(eventType);
    }


    public FlinkSplitFunctionBuilder<T> operatorName(String operatorName) {
        this.operatorName = configManager.getOPName(operatorName);
        return this;
    }

    public FlinkSplitFunctionBuilder<T> parallelism(String parallelism) {
        this.parallelism = configManager.getParallelism(parallelism);
        return this;
    }

    public FlinkSplitFunctionBuilder<T> uid(String uid) {
        this.uid = configManager.getOPUid(uid);
        return this;
    }

    public FlinkSplitFunctionBuilder<T> slotGroup(String slotGroup) {
        this.slotGroup = configManager.getStrDirect(slotGroup);
        return this;
    }

    public FlinkSplitFunctionBuilder<T> process(ProcessFunction<T,T> processFunction) {
        this.processFunction = processFunction;
        return this;
    }

    public SingleOutputStreamOperator<T> build() {
        return dataStream.rescale().process(processFunction)
                .setParallelism(parallelism)
                .name(operatorName)
                .slotSharingGroup(slotGroup)
                .uid(uid)
                .setMaxParallelism(maxParallelism);
    }
}
