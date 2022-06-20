package com.ebay.epic.common.env;

import com.ebay.epic.utils.Property;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ebay.epic.utils.Property.YAML_EXT;
import static com.ebay.epic.utils.Property.YML_EXT;

@Slf4j
public class PropertySource extends AbstractEnvironment {

    private final String configFileName;
    private final Integer order;

    public PropertySource() {
        this(Property.BASE_CONFIG, 999);
    }

    public PropertySource(String configFileName, Integer order) {
        this.configFileName = configFileName;
        this.order = order;
        sourceProps();
        log.info("Loaded Properties: {}", props.entrySet().stream().map(x -> String.format("%s->%s", x.getKey(), x.getValue().toString())).collect(Collectors.joining(",")));
    }

    private void sourceProps() {

        InputStream yaml = this.getClass().getClassLoader()
                .getResourceAsStream(configFileName + YML_EXT);
        if (yaml == null) {
            yaml = this.getClass().getClassLoader().getResourceAsStream(configFileName + YAML_EXT);
        }
        if (yaml != null) {
            ObjectMapper objectMapper =
                    new ObjectMapper(new YAMLFactory())
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                props = objectMapper.readValue(yaml, new TypeReference<Map<String, Object>>() {
                });
                props = flattenProps(props);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Integer order() {
        return order;
    }

    private Map<String, Object> flattenProps(Map<String, Object> props) {
        Map<String, Object> flattenedProps = Maps.newHashMap();

        for (String key : props.keySet()) {
            if (props.get(key) instanceof Map) {
                nestReplace(key, (Map<String, Object>) props.get(key), flattenedProps);
            } else {
                flattenedProps.put(key, props.get(key));
            }
        }
        return flattenedProps;
    }

    private void nestReplace(String key, Map<String, Object> map, Map<String, Object> newProps) {
        for (String mapKey : map.keySet()) {
            String newKey = String.format("%s.%s", key, mapKey);
            if (map.get(mapKey) instanceof Map) {
                nestReplace(newKey, (Map<String, Object>) map.get(mapKey), newProps);
            } else {
                newProps.put(newKey, map.get(mapKey));
            }
        }
    }
}
