package com.ebay.epic.common.env;

import lombok.ToString;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@ToString
public abstract class AbstractEnvironment implements Environment {

    @ToString.Include(rank = 99)
    protected Map<String, Object> props = new HashMap<>();

    @Override
    public boolean contains(String key) {
        return props.containsKey(key);
    }

    @Nullable
    @Override
    public String getProperty(String key) {
        return String.valueOf(props.get(key)).trim();
    }

    @Nullable
    @Override
    public <T> T getProperty(String key, Class<T> clazz) {
        return (T) props.get(key);
    }

    @ToString.Include
    public abstract Integer order();

}
