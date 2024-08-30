package com.ebay.epic.soj.business.metric;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public abstract class RecordMetrics<Source, Target> implements Aggregator<Source, Target> {

  protected Set<FieldMetrics<Source, Target>> fieldMetrics
      = new CopyOnWriteArraySet<>();

  /**
   * Initialize the field metrics for being used in aggregator operations.
   */
  public abstract void initFieldMetrics();

  public void init() throws Exception {
    for (FieldMetrics<Source, Target> metrics : fieldMetrics) {
      metrics.init();
    }
  }

  public void start(Target target) throws Exception {
    for (FieldMetrics<Source, Target> metrics : fieldMetrics) {
      metrics.start(target);
    }
  }

  @Override
  public void process(Source source, Target target) throws Exception {
    for (FieldMetrics<Source, Target> metrics : fieldMetrics) {
      try {
        metrics.feed(source, target);
      }catch(Exception e){
        log.warn(" session metric feed issue :{},metric name is {}",e, metrics.getClass().getName());
      }
    }
  }
  public void end(Target target) throws Exception {
    for (FieldMetrics<Source, Target> metrics : fieldMetrics) {
      metrics.end(target);
    }
  }

  public void addFieldMetrics(FieldMetrics<Source, Target> metrics) {
    // TODO: this line is always true sine FieldMetrics does not override equals()
    if (!fieldMetrics.contains(metrics)) {
      fieldMetrics.add(metrics);
    } else {
      throw new RuntimeException("Duplicate Metrics!");
    }
  }
}
