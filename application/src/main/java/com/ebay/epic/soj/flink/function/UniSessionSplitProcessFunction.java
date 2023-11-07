package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSession;
import com.ebay.epic.soj.flink.constant.OutputTagConstants;
import com.google.common.collect.Lists;
import io.jsonwebtoken.lang.Collections;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;

public class UniSessionSplitProcessFunction extends ProcessFunction<UniSession,UniSession> {

    @Override
    public void processElement(UniSession t, Context context, Collector<UniSession> out) {
        if(!Collections.isEmpty(detectBot(t))){
            for(String category:detectBot(t)){
                context.output(OutputTagConstants.outputTagMapSess.get(category),t);
            }
        }
        out.collect(null);
    }

    private List<String> detectBot(UniSession t){
        List<String> category = Lists.newArrayList();
        if(t.getBotFlag().getSurface().size()==1&&t.getBotFlag().getSurface().contains(0)
        &&t.getBotFlag().getUbi().size()==1&&t.getBotFlag().getUbi().contains(0)
        &&t.getBotFlag().getUtp().size()==1&&t.getBotFlag().getUtp().contains(0)){
            if(t.getOthers().containsKey("issesslkp")&&t.getOthers().get("issesslkp").equals("1")) {
                category.add(EventType.SESSION_LOOKUP_NONBOT.getFullName());
            }
            if(!(t.getOthers().containsKey("issesslkp"))||t.getIsOpen())
            {
                category.add(EventType.SESSION_NONBOT.getFullName());
            }
        }else{
            if(t.getOthers().containsKey("issesslkp")&&t.getOthers().get("issesslkp").equals("1")) {
                category.add(EventType.SESSION_LOOKUP_NONBOT.getFullName());
            }
            if(!(t.getOthers().containsKey("issesslkp"))||t.getIsOpen())
            {
                category.add(EventType.SESSION_NONBOT.getFullName());
            }
            // TODO need to enable bot detection and make some enhancement
            //            return EventType.SESSION_BOT.getFullName();
        }
        return category;
    }
}
