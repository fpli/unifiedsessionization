package org.apache.flink.streaming.runtime.operators.windowing;

import org.apache.flink.api.common.functions.Function;

public interface MapWithStateFunction<T, S, O> extends Function {

  O map(T value, S state) throws Exception;
}
