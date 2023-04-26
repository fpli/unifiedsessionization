package com.ebay.epic.soj.flink.function;

import com.ebay.epic.common.constant.OutputTagConstants;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.UniSession;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.flink.util.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniSessionSplitProcessFunction extends ProcessFunction<UniSession,UniSession> {

    @Override
    public void processElement(UniSession t, Context context, Collector<UniSession> out) {
        context.output(OutputTagConstants.outputTagMapSess.get(detectBot(t)),t);
        out.collect(null);
    }

    private String detectBot(UniSession t){
        if(t.getBotFlag().getSurface().size()==1&&t.getBotFlag().getSurface().contains(0)
        &&t.getBotFlag().getUbi().size()==1&&t.getBotFlag().getUbi().contains(0)
        &&t.getBotFlag().getUtp().size()==1&&t.getBotFlag().getUtp().contains(0)){
            return EventType.SESSION_NONBOT.getFullName();
        }else{
            return EventType.SESSION_NONBOT.getFullName();
            // TODO need to enable bot detection and make some enhancement
            //            return EventType.SESSION_BOT.getFullName();
        }
    }
}
