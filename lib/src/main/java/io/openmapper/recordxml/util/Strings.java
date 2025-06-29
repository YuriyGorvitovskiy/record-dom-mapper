package io.openmapper.recordxml.util;

import java.util.function.Supplier;

public interface Strings {

    String EMPTY = "";

    static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    static String whenEmpty(String value, Supplier<String> onEmpty) {
        return isEmpty(value) ? onEmpty.get() : value;
    }

    static boolean notEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
