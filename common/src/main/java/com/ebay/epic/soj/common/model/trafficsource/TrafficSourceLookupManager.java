package com.ebay.epic.soj.common.model.trafficsource;

import com.ebay.epic.soj.common.model.lookup.PageFamilyAll;
import com.ebay.epic.soj.common.model.lookup.PageFamilyInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TrafficSourceLookupManager implements Runnable {

    private static TrafficSourceLookupManager trafficSourceLookupManager =
            new TrafficSourceLookupManager();
    private volatile Pages pages;
    private volatile Rotations rotations;
    private volatile PageFamilyAll pageFamilyAll;

    private TrafficSourceLookupManager() {
        initializeLookups();
        scheduleRefreshLookups();
    }

    private void initializeLookups() {
        Pages initPages = new Pages();
        Rotations initRotations = new Rotations();
        PageFamilyAll initPageFamilyAll = new PageFamilyAll();
        if (initPages.initialize() && initRotations.initialize() && initPageFamilyAll.initialize()) {
            pages = initPages;
            rotations = initRotations;
            pageFamilyAll = initPageFamilyAll;
            log.info("Lookups are initialized successfully.");
            log.info("pages: " + pages);
            log.info("rotations: " + rotations);
            log.info("pageFamilyAll: " + pageFamilyAll);
        } else {
            throw new RuntimeException("Failed to initialize lookups.");
        }
    }

    private void scheduleRefreshLookups() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor
                = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread t = new Thread(r, "traffic-source-lookup-thread");
                t.setDaemon(true);
                return t;
            }
        });
        scheduledThreadPoolExecutor
                .scheduleAtFixedRate(this, 60, 60, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        try {
            refreshLookups();
        } catch (Exception e) {
            log.warn("Failed to refresh lookups", e);
        }
    }

    private void refreshLookups() {
        if (pages.outOfDate()) {
            Pages newPages = new Pages();
            if (newPages.loadFromHdfs() == LoadStatus.SUCCESS) {
                pages = newPages;
                log.info("pages refreshed successfully: " + pages);
            }
        }
        if (rotations.outOfDate()) {
            Rotations newRotations = new Rotations();
            if (newRotations.loadFromHdfs() == LoadStatus.SUCCESS) {
                rotations = newRotations;
                log.info("rotations refreshed successfully: " + rotations);
            }
        }
        if (pageFamilyAll.outOfDate()) {
            PageFamilyAll newPageFamilyAll = new PageFamilyAll();
            if (newPageFamilyAll.loadStatus == LoadStatus.SUCCESS) {
                pageFamilyAll = newPageFamilyAll;
                log.info("pageFamilyAll refreshed successfully: " + pageFamilyAll);
            }
        }
    }

    public static TrafficSourceLookupManager getInstance() {
        return trafficSourceLookupManager;
    }

    public Map<Long, DwMpxRotation> getDwMpxRotationMap() {
        return rotations.getKvMap();
    }

    public Map<Integer, Page> getPageMap() {
        return pages.getKvMap();
    }

    public Map<Integer, PageFamilyInfo> getPageFamilyAllMap() {
        return pageFamilyAll.getKvMap();
    }

}
