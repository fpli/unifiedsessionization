package com.ebay.epic.common.env;

import java.util.Map;

public class EnvSource extends AbstractEnvironment {

  @Override
  public void sourceProps() {
    Map<String, String> getenv = System.getenv();
    getenv.forEach((key, value) -> {
      String newKey = key.replace("_", ".").toLowerCase();
      props.put(newKey, value);
    });
  }

  @Override
  public Integer order() {
    return 2;
  }
}
