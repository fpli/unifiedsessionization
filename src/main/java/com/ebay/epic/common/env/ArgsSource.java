package com.ebay.epic.common.env;

import lombok.extern.slf4j.Slf4j;

import java.util.Enumeration;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class ArgsSource extends AbstractEnvironment {

    private final Properties properties;

    public ArgsSource(Properties properties) {
        this.properties = properties;
        sourceProps();
        log.info("Loaded Args: {}", props.entrySet().stream().map(x -> String.format("%s->%s", x.getKey(), x.getValue().toString())).collect(Collectors.joining(",")));
    }

    private void sourceProps() {

        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            this.props.put(key, properties.get(key));
        }
    }

    @Override
    public Integer order() {
        return 1;
    }
}
