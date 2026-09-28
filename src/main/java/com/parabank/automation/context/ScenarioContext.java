package com.parabank.automation.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * ScenarioContext manages scenario-scoped state shared across step definitions.
 * <p>
 * Uses a {@link ThreadLocal} for thread-safety during parallel execution.
 * Step definitions call {@link #current()} to obtain the instance — no DI
 * container or constructor injection required (zero-arg instantiation).
 * Call {@link #clear()} in the Cucumber {@code @After} hook to reset state
 * between scenarios.
 */
public final class ScenarioContext {

    private static final Logger log = LoggerFactory.getLogger(ScenarioContext.class);

    /** One isolated context map per thread. */
    private static final ThreadLocal<ScenarioContext> INSTANCE =
            ThreadLocal.withInitial(ScenarioContext::new);

    private final Map<ContextKey, Object> contextMap = new EnumMap<>(ContextKey.class);

    // Private — callers must use current()
    private ScenarioContext() {}

    /** Returns the thread-bound {@code ScenarioContext} instance. */
    public static ScenarioContext current() {
        return INSTANCE.get();
    }

    /** Resets state at end of scenario and removes the ThreadLocal entry. */
    public static void reset() {
        log.debug("ScenarioContext reset for thread '{}'.", Thread.currentThread().getName());
        INSTANCE.remove();          // removes entry & allows GC; next call re-creates
    }

    // -----------------------------------------------------------------------
    // State accessors
    // -----------------------------------------------------------------------

    public void set(ContextKey key, Object value) {
        log.debug("ScenarioContext [{} = {}]", key, value);
        contextMap.put(key, value);
    }

    public Object get(ContextKey key) {
        return contextMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ContextKey key, Class<T> clazz) {
        Object val = contextMap.get(key);
        if (val == null) return null;
        if (!clazz.isInstance(val)) {
            throw new ClassCastException("Context key " + key
                    + " expected " + clazz.getName()
                    + " but was " + val.getClass().getName());
        }
        return (T) val;
    }

    public String getString(ContextKey key) {
        Object val = contextMap.get(key);
        return val != null ? val.toString() : null;
    }

    public Double getDouble(ContextKey key) {
        Object val = contextMap.get(key);
        if (val instanceof Number n) return n.doubleValue();
        if (val instanceof String s)
            return Double.parseDouble(s.replace("$", "").replace(",", "").trim());
        return null;
    }

    public boolean contains(ContextKey key) {
        return contextMap.containsKey(key);
    }

    /** @deprecated Use {@link #reset()} from the {@code @After} hook instead. */
    @Deprecated
    public void clear() {
        contextMap.clear();
    }
}
