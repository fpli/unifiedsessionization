package com.ebay.epic.soj.business.filter;

public interface IFilter<T> {

    default void init() throws Exception {
    }

    boolean filter(T t) throws Exception;

    default void close() throws Exception {
    }
}
