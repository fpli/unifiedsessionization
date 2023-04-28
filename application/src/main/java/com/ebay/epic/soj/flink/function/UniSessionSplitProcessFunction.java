package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSession;
import com.ebay.epic.soj.flink.constant.OutputTagConstants;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

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
