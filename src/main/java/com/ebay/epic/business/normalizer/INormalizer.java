package com.ebay.epic.business.normalizer;

public interface INormalizer<Source, Target> {

    default void init() throws Exception {
    }

    void normalize(Source src, Target tar) throws Exception;

    default void close() throws Exception {
    }
}
