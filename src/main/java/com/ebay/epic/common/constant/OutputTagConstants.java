package com.ebay.epic.common.constant;

import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.RawUniSession;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.OutputTag;

public class OutputTagConstants {

  public static OutputTag<RawUniSession> sessionOutputTag =
      new OutputTag<>("session-output-tag", TypeInformation.of(RawUniSession.class));

  public static OutputTag<RawEvent> lateEventOutputTag =
      new OutputTag<>("late-event-output-tag", TypeInformation.of(RawEvent.class));

  public static OutputTag<RawEvent> mappedEventOutputTag =
      new OutputTag<>("mapped-event-output-tag", TypeInformation.of(RawEvent.class));

  public static OutputTag<RawEvent> dataSkewOutputTag =
      new OutputTag<>("skew-raw-event-output-tag", TypeInformation.of(RawEvent.class));

}
