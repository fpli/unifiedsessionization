package com.ebay.epic.soj.flink.pipeline.it;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

@Slf4j
public class TrafficSourceLookupManagerITJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.disableOperatorChaining();

        env.addSource(new TestEventSource()).name("source").uid("source")
                .process(new TestProcessFunction()).name("process").uid("process")
                .addSink(new DiscardingSink<>()).name("sink").uid("sink");
        env.execute("TrafficSourceLookupManager IT Job");
    }

}
