package com.ebay.epic.business.normalizer;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class RecordNormalizer<Source, Target> implements INormalizer<Source, Target> {
    protected transient List<FieldNormalizer<Source, Target>> fieldNormalizers = new ArrayList<>();

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
            try {
                fieldNormalizer.normalize(source, target);
            } catch (Exception e) {
                log.error("{} gets exception", fieldNormalizer.getClass().getCanonicalName(), e);
            }

        }
    }

    public void addFieldNormalizer(FieldNormalizer<Source, Target> normalizer) {
        if (!fieldNormalizers.contains(normalizer)) {
            fieldNormalizers.add(normalizer);
        } else {
            log.error("{} Duplicate Normalizer,discard it", normalizer.getClass().getCanonicalName());
        }
    }

    @Override
    public void close() throws Exception {
        for (FieldNormalizer<Source, Target> normalizer : fieldNormalizers) {
            normalizer.close();
        }
    }
}
