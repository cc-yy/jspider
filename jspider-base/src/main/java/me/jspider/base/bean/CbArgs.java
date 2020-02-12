package me.jspider.base.bean;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A class containing arguments for trespassing.
 */
public class CbArgs {
    private final Map<String, Object> args;

    public CbArgs() {
        this.args = Maps.newHashMap();
    }

    private CbArgs(Map<String, Object> args) {
        this.args = args;
    }

    public CbArgs(CbArgs args) {
        this.args = Maps.newHashMap(args.args);
    }

    public <T> void set(String key, T value) {
        args.put(key, value);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return args.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) args.get(key);
    }

    public static class Builder {
        private final Map<String, Object> args = Maps.newHashMap();

        private Builder() {
        }

        public <T> Builder set(String key, T value) {
            args.put(key, value);
            return this;
        }

        public CbArgs build() {
            return new CbArgs(this.args);
        }
    }
}
