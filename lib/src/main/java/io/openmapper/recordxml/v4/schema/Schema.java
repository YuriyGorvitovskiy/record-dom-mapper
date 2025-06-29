package io.openmapper.recordxml.v4.schema;

import io.openmapper.recordxml.v4.dynamic.EntryMapper;

public record Schema() {
    public static <T> Schema of(Class<T> rawClass) {
        return new Schema();
    }

    public <T> EntryMapper root(Class<T> rawType) {

    }
}
