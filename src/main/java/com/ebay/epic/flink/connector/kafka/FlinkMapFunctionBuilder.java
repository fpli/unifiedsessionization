package com.ebay.epic.flink.connector.kafka;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.flink.connector.kafka.config.ConfigManager;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import static com.ebay.epic.utils.FlinkEnvUtils.getInteger;
import static com.ebay.epic.utils.Property.DEFAULT_PARALLELISM;

public class FlinkMapFunctionBuilder<IN,OUT> {

    private final DataStream<IN> dataStream;
    private DataCenter dc;
    private String operatorName;
    private String uid;
    private String sinkSlotGroup;
    private int parallelism = getInteger(DEFAULT_PARALLELISM);
    private EventType eventType;
    public static final String DELEMITER=".";
    private ConfigManager configManager;
    private RichMapFunction<IN,OUT> richFilterFunction;
    private Boolean isDrived;
    public FlinkMapFunctionBuilder(DataStream<IN> dataStream, DataCenter dc, EventType eventType, Boolean isDrived) {
        this.dataStream = dataStream;
        this.dc=dc;
        this.eventType=eventType;
        this.configManager = new ConfigManager(eventType);
        this.configManager.setDrived(isDrived);
    }

    public FlinkMapFunctionBuilder<IN,OUT> operatorName(String operatorName) {
        this.operatorName = configManager.getStrValueNODC(operatorName) +" " +eventType.getValue();
        return this;
    }

    public FlinkMapFunctionBuilder<IN,OUT> parallelism(String parallelism) {
        this.parallelism = configManager.getParallelism(parallelism);
        return this;
    }

    public FlinkMapFunctionBuilder<IN,OUT> uid(String uid) {
        this.uid = configManager.getStrValueNODC(uid) +" " +eventType.getValue();
        return this;
    }

    public FlinkMapFunctionBuilder<IN,OUT> slotGroup(String slotGroup) {
        this.sinkSlotGroup = configManager.getSlotSharingGroup(slotGroup);
        return this;
    }
    public FlinkMapFunctionBuilder<IN,OUT> map(RichMapFunction<IN,OUT> mapFunction) {
        this.richFilterFunction= mapFunction;
        return this;
    }

    public SingleOutputStreamOperator<OUT> build() {
       return dataStream.map(richFilterFunction)
                .setParallelism(parallelism)
                .name(operatorName)
                .uid(uid);
    }
}
