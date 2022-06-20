package com.ebay.epic.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yimikong
 * @date 4/13/22
 */
@Slf4j
public class ParquetUtils {
    private static final ThreadLocal<FileSystem> fileSystem = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> inUsage = new ThreadLocal<>();

    private final static Map<String, Map<String, Long>> FILE_STATUS = Maps.newConcurrentMap();

    public static FileSystem getFileSystem() throws IOException {
        if (fileSystem.get() == null) {
            synchronized (ParquetUtils.class) {
                if (fileSystem.get() == null) {
                    fileSystem.set(FileSystem.get(new Configuration()));
                }
            }
        }
        if (inUsage.get() == null) {
            synchronized (ParquetUtils.class) {
                if (inUsage.get() == null) {
                    inUsage.set(new AtomicInteger(0));
                }
            }
        }
        log.info("open and in usage count {}", inUsage.get().incrementAndGet());
        return fileSystem.get();
    }

    public static void closeIfNeed() throws IOException {
        if (inUsage.get() != null
                && inUsage.get().decrementAndGet() <= 0) {
            log.info("close and in usage count {}", inUsage.get().get());
            synchronized (ParquetUtils.class) {
                if (fileSystem.get() != null) {
                    fileSystem.get().close();
                    fileSystem.set(null);
                }
            }
        }
    }

    public static <T> List<T> readParquetFile(Path path, Class<T> tClass) {
        FileStatus[] fileStatuses;
        try {
            fileStatuses = getFileSystem().listStatus(path);
        } catch (Exception e) {
            log.error("failed to list parquets", e);
            return null;
        }
        if (!needRefresh(fileStatuses)) {
            log.info("No need to refresh,file status: {}", FILE_STATUS);
            return null;
        }
        Configuration conf = new Configuration();
        List<T> result = new ArrayList<>();

        for (FileStatus fileStatus : fileStatuses) {
            Path parquetPath = fileStatus.getPath();
            FILE_STATUS.computeIfAbsent(parquetPath.getParent().getName(), (e) -> Maps.newConcurrentMap())
                    .put(parquetPath.getName(), fileStatus.getModificationTime());
            if (!StringUtils.endsWith(parquetPath.getName(), ".parquet")) {
                continue;
            }
            log.info("try to load file {}", parquetPath.getName());
            try {
                InputFile inputFile = HadoopInputFile.fromPath(parquetPath, conf);
                try (ParquetFileReader reader = ParquetFileReader.open(inputFile)) {
                    PageReadStore pageReadStore = reader.readNextRowGroup();

                    while (pageReadStore != null) {
                        ParquetMetadata readFooter = reader.getFooter();
                        MessageType schema = readFooter.getFileMetaData().getSchema();
                        MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);

                        long currentRemainingSize = pageReadStore.getRowCount();
                        log.info("Non-null chunk, size: {}", currentRemainingSize);
                        RecordReader<Group> recordReader = columnIO.getRecordReader(pageReadStore, new GroupRecordConverter(schema));

                        for (int i = 0; i < currentRemainingSize; i++) {
                            T record = convertGroup(recordReader.read(), tClass);
                            if (Objects.nonNull(record)) {
                                result.add(record);
                            }
                        }

                        pageReadStore = reader.readNextRowGroup();
                    }
                }
            } catch (Exception e) {
                log.error("failed to read {}", path, e);
            }
        }
        try {
            closeIfNeed();
        } catch (Exception e) {
            log.error("failed to close filesystem", e);
        }
        return result;
    }

    private static boolean needRefresh(FileStatus[] fileStatuses) {
        for (FileStatus status : fileStatuses) {
            String parent = status.getPath().getParent().getName();
            if (FILE_STATUS.containsKey(parent)) {
                Map<String, Long> statusMap = FILE_STATUS.get(parent);
                String fileName = status.getPath().getName();
                if (statusMap.containsKey(fileName)) {
                    if (status.getModificationTime() > statusMap.get(fileName)) {
                        statusMap.clear();
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static <T> T convertGroup(Group read, Class<T> tClass) {
        if (read == null) {
            return null;
        }
        try {
            T tar = tClass.newInstance();

            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isFinal(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = field.getName();

                ParquetColumnName[] annotations = field.getAnnotationsByType(ParquetColumnName.class);
                if (annotations.length > 0) {
                    ParquetColumnName columnAno = annotations[0];
                    columnName = columnAno.value();
                }
                if (read.getFieldRepetitionCount(columnName) != 1) {
                    continue;
                }
                field.setAccessible(true);

                if (ClassUtils.isAssignable(field.getType(), String.class)) {
                    field.set(tar, read.getString(columnName, 0));
                } else if (ClassUtils.isAssignable(field.getType(), Integer.class)) {
                    field.set(tar, read.getInteger(columnName, 0));
                }// TODO support more data type: float,double,boolean,group
            }
            return tar;
        } catch (Exception e) {
            log.error("failed to convert group", e);
        }
        return null;
    }
}
