package com.ebay.epic.common.env;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class EnvironmentUtils {

    private static final Set<AbstractEnvironment> PROP_SOURCES =
            Sets.newTreeSet(Comparator.comparing(AbstractEnvironment::order));


    static {
        log.info("Load default environment properties file");
        PROP_SOURCES.add(new EnvSource());
        PROP_SOURCES.add(new EnvSource.PropertySource());
        // source props
        for (AbstractEnvironment propSource : PROP_SOURCES) {
            propSource.sourceProps();
        }
    }

    public static void activateProfile(String profile) {
        Preconditions.checkNotNull(profile);
        String configFileName = "application-" + profile;
        EnvSource.PropertySource propertySource = new EnvSource.PropertySource(configFileName, 3);
        propertySource.sourceProps();
        PROP_SOURCES.add(propertySource);
    }

    public static void fromProperties(Properties properties) {
        Preconditions.checkNotNull(properties);
        ArgsSource argsSource = new ArgsSource(properties);
        argsSource.sourceProps();
        PROP_SOURCES.add(argsSource);
    }

    public static String get(String key) {
        Preconditions.checkNotNull(key);
        for (AbstractEnvironment propSource : PROP_SOURCES) {
            if (propSource.contains(key)) {
                return propSource.getProperty(key);
            }
        }
        throw new IllegalStateException("Cannot find property " + key);
    }

    public static String[] getStringArray(String key, String delimiter) {
        String s = get(key);
        delimiter = "\\s*" + delimiter + "\\s*";
        return s.split(delimiter);
    }

    public static List<String> getStringList(String key, String delimiter) {
        String[] stringArray = getStringArray(key, delimiter);
        return Lists.newArrayList(stringArray);
    }

    public static String getStringOrDefault(String key, String defaultValue) {
        for (AbstractEnvironment propSource : PROP_SOURCES) {
            if (propSource.contains(key)) {
                return propSource.getProperty(key);
            }
        }
        return defaultValue;
    }

    public static Boolean getBoolean(String key) {
        String booleanVal = get(key);
        return Boolean.valueOf(booleanVal);
    }

    public static Integer getInteger(String key) {
        String intVal = get(key);
        return Integer.valueOf(intVal);
    }

    public static <T> T get(String key, Class<T> clazz) {
        for (AbstractEnvironment propSource : PROP_SOURCES) {
            if (propSource.contains(key)) {
                return propSource.getProperty(key, clazz);
            }
        }
        throw new IllegalStateException("Cannot find property " + key);
    }
}