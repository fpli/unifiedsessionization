package com.ebay.epic.soj.business.normalizer;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public abstract class RecordNormalizer<Source, Target> implements INormalizer<Source, Target> {

    protected transient Set<FieldNormalizer<Source, Target>> fieldNormalizers = new LinkedHashSet<>();

    public abstract void initFieldNormalizers();

    @Override
    public void init() throws Exception {
        initFieldNormalizers();
        for (FieldNormalizer<Source, Target> normalizer : fieldNormalizers) {
            normalizer.init();
        }
    }

    @Override
    public void normalize(Source source, Target target) throws Exception {
        for (FieldNormalizer<Source, Target> fieldNormalizer : fieldNormalizers) {
            if(fieldNormalizer.accept(source)) {
                try {
                    fieldNormalizer.normalize(source, target);
                } catch (Exception e) {
                    log.error("{} gets exception", fieldNormalizer.getClass().getCanonicalName(), e);
                }
            }
        }
    }

    public void addFieldNormalizer(FieldNormalizer<Source, Target> normalizer) {
        fieldNormalizers.add(normalizer);
    }

    @Override
    public void close() throws Exception {
        for (FieldNormalizer<Source, Target> normalizer : fieldNormalizers) {
            normalizer.close();
        }
    }
}
