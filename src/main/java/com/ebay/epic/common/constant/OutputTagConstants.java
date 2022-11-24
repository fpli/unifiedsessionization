package com.ebay.epic.common.constant;

import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.ebay.epic.common.model.raw.UniEvent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.OutputTag;

public class OutputTagConstants {

  public static OutputTag<RawUniSession> sessionOutputTag =
      new OutputTag<>("session-output-tag", TypeInformation.of(RawUniSession.class));

  public static OutputTag<UniEvent> lateEventOutputTag =
      new OutputTag<>("late-event-output-tag", TypeInformation.of(UniEvent.class));

  public static OutputTag<UniEvent> mappedEventOutputTag =
      new OutputTag<>("mapped-event-output-tag", TypeInformation.of(UniEvent.class));

  public static OutputTag<UniEvent> dataSkewOutputTag =
      new OutputTag<>("skew-raw-event-output-tag", TypeInformation.of(UniEvent.class));

}
