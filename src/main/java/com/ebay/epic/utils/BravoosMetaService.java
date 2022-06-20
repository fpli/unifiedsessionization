package com.ebay.epic.utils;

import com.ebay.epic.common.constant.Constants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.fs.Path;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public final class BravoosMetaService implements AutoCloseable {
    private final static Long REFRESH_PERIOD = 120L;

    private final static class InstanceHolder {
        private static final BravoosMetaService INSTANCE = new BravoosMetaService();
    }

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<Map<Integer, PageMetadata>> pageLookup = new AtomicReference<>(Maps.newHashMap());
    private final AtomicReference<Map<Integer, ModuleMetadata>> moduleLookup = new AtomicReference<>(Maps.newHashMap());
    private final AtomicReference<Map<Integer, ClickMetadata>> clickLookup = new AtomicReference<>(Maps.newHashMap());

    private BravoosMetaService() {
        refreshPage();
        refreshModule();
        refreshClick();
        executorService.scheduleWithFixedDelay(this::refreshPage, REFRESH_PERIOD, REFRESH_PERIOD, TimeUnit.MINUTES);
        executorService.scheduleWithFixedDelay(this::refreshModule, REFRESH_PERIOD, REFRESH_PERIOD, TimeUnit.MINUTES);
        executorService.scheduleWithFixedDelay(this::refreshClick, REFRESH_PERIOD, REFRESH_PERIOD, TimeUnit.MINUTES);
    }

    public static BravoosMetaService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void refreshPage() {
        refreshMap(pageLookup, Constants.PAGE_METADATA_PATH, PageMetadata::getPageId, PageMetadata.class);
        log.info("page look up table size: {}", pageLookup.get().size());
    }

    private void refreshModule() {
        refreshMap(moduleLookup, Constants.MODULE_METADATA_PATH, ModuleMetadata::getModuleId, ModuleMetadata.class);
        log.info("module look up table size: {}", moduleLookup.get().size());
    }

    private void refreshClick() {
        refreshMap(clickLookup, Constants.CLICK_METADATA_PATH, ClickMetadata::getClickId, ClickMetadata.class);
        log.info("click look up table size: {}", clickLookup.get().size());
    }

    private <T> void refreshMap(AtomicReference<Map<Integer, T>> ref, String path, ExtraKey<T> extraKey, Class<T> tClass) {
        val filePath = new Path(path);

        val metas = ParquetUtils.readParquetFile(filePath, tClass);

        if (CollectionUtils.isEmpty(metas)) {
            return;
        }

        Map<Integer, T> res = Maps.newHashMap();
        for (T meta : metas) {
            res.put(extraKey.extra(meta), meta);
        }

        ref.set(res);
    }

    private interface ExtraKey<T> {
        Integer extra(T e);
    }

    public PageMetadata getPageMeta(Integer pageId) {
        if (pageId == null) {
            return null;
        }
        return pageLookup.get().get(pageId);
    }

    public ModuleMetadata getModuleMeta(Integer moduleId) {
        if (moduleId == null) {
            return null;
        }
        return moduleLookup.get().get(moduleId);
    }

    public ClickMetadata getClickMeta(Integer clickId) {
        if (clickId == null) {
            return null;
        }
        return clickLookup.get().get(clickId);
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
        pageLookup.set(Maps.newHashMap());
        moduleLookup.set(Maps.newHashMap());
        clickLookup.set(Maps.newHashMap());
    }
}
