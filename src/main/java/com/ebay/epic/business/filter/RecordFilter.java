package com.ebay.epic.business.filter;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public abstract class RecordFilter<T> implements IFilter<T> {

    protected transient List<CombinationFilter<T>> combinationFilters;

    public abstract void initCombinationFilters();

    @Override
    public void init() throws Exception {
        combinationFilters = new ArrayList<>();
        initCombinationFilters();
        for (CombinationFilter<T> combinationFilter : combinationFilters) {
            combinationFilter.init();
        }
    }

    @Override
    public boolean filter(T t) throws Exception {
        Set<Boolean> filterresults = new HashSet<>();
        for (CombinationFilter<T> combinationFilter : combinationFilters) {
            try {
                boolean result = combinationFilter.filter(t);
                filterresults.add(result);
                if (filterresults.contains(false)) {
                    return false;
                }
            } catch (Exception e) {
                log.error("{} gets exception", combinationFilter.getClass().getCanonicalName(), e);
                return false;
            }
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        for (CombinationFilter<T> combinationFilter : combinationFilters) {
            combinationFilter.close();
        }
    }

    public void addCombinationFilters(CombinationFilter<T> combinationFilter) {
        if (!combinationFilters.contains(combinationFilter)) {
            combinationFilters.add(combinationFilter);
        } else {
            log.error("{} Duplicate Filter,discard it", combinationFilter.getClass().getCanonicalName());
        }
    }

}
