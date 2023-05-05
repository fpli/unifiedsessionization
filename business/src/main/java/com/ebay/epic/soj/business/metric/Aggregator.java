package com.ebay.epic.soj.business.metric;

/**
 * The aggregation
 *
 * @author xiaoding
 */
public interface Aggregator<Source, Target> {

  /**
   * init the aggregator
   */
  default void init() throws Exception{};

  /**
   * Start is the start point to aggregate the source for the target.
   */
  default void start(Target target) throws Exception{};

  /**
   * Feed the source to be aggregated for the target.
   */
  default void feed(Source source, Target target) throws Exception{
    if(accept(source)){
      process(source,target);
    }
  };

  default boolean accept(Source source){return true;};

  void process(Source source,Target target) throws  Exception;

  /**
   *
   */
  default void end(Target target) throws Exception{};
}
