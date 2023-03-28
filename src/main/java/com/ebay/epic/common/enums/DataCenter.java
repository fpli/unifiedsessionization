package com.ebay.epic.common.enums;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Getter
public enum DataCenter {
  RNO("RNO"),
  SLC("SLC"),
  LVS("LVS"),
  DEFAULT("");

  private final String value;

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  public static DataCenter of(String dataCenter) {
    Preconditions.checkArgument(StringUtils.isNotBlank(dataCenter));
    return DataCenter.valueOf(dataCenter.toUpperCase());
  }
}
