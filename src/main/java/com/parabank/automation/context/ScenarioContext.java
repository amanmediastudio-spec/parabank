package com.parabank.automation.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * ScenarioContext manages scenario-scoped state across multiple step definitions.
 * Automatically instantiated and injected by cucumber-picocontainer per Scenario.
 */
public class ScenarioContext {
    private static final Logger log = LoggerFactory.getLogger(ScenarioContext.class);
    private final Map<ContextKey, Object> contextMap = new EnumMap<>(ContextKey.class);

    public void set(ContextKey key, Object value) {
        log.debug("Setting ScenarioContext [{} = {}]", key, value);
        contextMap.put(key, value);
    }

    public Object get(ContextKey key) {
        return contextMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ContextKey key, Class<T> clazz) {
        Object val = contextMap.get(key);
        if (val == null) {
            return null;
        }
        if (!clazz.isInstance(val)) {
            throw new ClassCastException("Context key " + key + " expected " + clazz.getName() + " but was " + val.getClass().getName());
        }
        return (T) val;
    }

    public String getString(ContextKey key) {
        Object val = contextMap.get(key);
        return val != null ? val.toString() : null;
    }

    public Double getDouble(ContextKey key) {
        Object val = contextMap.get(key);
        if (val instanceof Number number) {
            return number.doubleValue();
        }
        if (val instanceof String str) {
            return Double.parseDouble(str.replace("$", "").replace(",", "").trim());
        }
        return null;
    }

    public boolean contains(ContextKey key) {
        return contextMap.containsKey(key);
    }

    public void clear() {
        contextMap.clear();
        log.debug("ScenarioContext cleared.");
    }
}
