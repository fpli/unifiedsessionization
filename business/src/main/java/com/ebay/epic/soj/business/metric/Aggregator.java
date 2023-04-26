package com.ebay.epic.soj.business.metric;

/**
 * The aggregation
 *
 * @author kofeng
 */
public interface Aggregator<Source, Target> {

  /**
   * init the aggregator
   */
  void init() throws Exception;

  /**
   * Start is the start point to aggregate the source for the target.
   */
  void start(Target target) throws Exception;

  /**
   * Feed the source to be aggregated for the target.
   */
  void feed(Source source, Target target) throws Exception;

  /**
   *
   */
  void end(Target target) throws Exception;
}
