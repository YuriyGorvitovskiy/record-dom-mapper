package io.openmapper.recordxml.util;

import io.vavr.CheckedFunction0;

public interface SoftenEx {
    static <R> R call(CheckedFunction0<R> supplier) {
        try {
            return supplier.apply();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
