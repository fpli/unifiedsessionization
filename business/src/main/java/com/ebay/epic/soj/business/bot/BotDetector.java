package com.ebay.epic.soj.business.bot;

public interface BotDetector<T> {
    default void init() throws Exception {};

    Long detectBot(T record );
    default void close() throws Exception {};

}
