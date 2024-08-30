package com.ebay.epic.soj.business.bot;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class BotRulesConfigLoader {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = BotRulesConfigLoader.class.getClassLoader().getResourceAsStream("botrules.properties")) {
            if (input == null) {
                throw new IOException("Sorry, unable to find config.properties");
            } else {
                //load a properties file from class path
                properties.load(input);
            }
        } catch (IOException ex) {
            log.error("Error loading config.properties", ex);
        }
    }

    public static int getIntOrDefault(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public static Map<Integer, Long> getRuleList(String keyPrefix) {
        Map<Integer, Long> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(keyPrefix)) {
                String mapKey = key.substring(keyPrefix.length() + 1);
                String value = properties.getProperty(key);
                map.put(Integer.parseInt(mapKey), Long.parseLong(value));
            }
        }
        return map;
    }

}