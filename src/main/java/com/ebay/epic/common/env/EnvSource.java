package com.ebay.epic.common.env;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class EnvSource extends AbstractEnvironment {

    public EnvSource() {
        sourceProps();
        log.info("Loaded Env: {}", props.entrySet().stream().map(x -> String.format("%s->%s", x.getKey(), x.getValue().toString())).collect(Collectors.joining(",")));
    }

    private void sourceProps() {
        Map<String, String> getenv = System.getenv();
        getenv.forEach((key, value) -> {
            String newKey = key.replace("_", ".").toLowerCase();
            props.put(newKey, value);
        });
    }

    @Override
    public Integer order() {
        return 2;
    }
}
