package com.ebay.epic.soj.flink.state;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.util.Map;

public class MapStateDesc {

  public static final MapStateDescriptor<String, Map<String, Map<Integer, Long[]>>>
      attributeSignatureDesc =
      new MapStateDescriptor<>(
          "broadcast-attributeSignature-state",
          BasicTypeInfo.STRING_TYPE_INFO,
          TypeInformation.of(new TypeHint<Map<String, Map<Integer, Long[]>>>() {
          }));

}
