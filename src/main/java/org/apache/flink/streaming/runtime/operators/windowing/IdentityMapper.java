package org.apache.flink.streaming.runtime.operators.windowing;

public class IdentityMapper<T> implements MapWithStateFunction<T, Object, T> {

  private static final long serialVersionUID = 1L;

  @Override
  public T map(T value, Object state) throws Exception {
    return value;
  }
}

