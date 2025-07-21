package io.openmapper.recordxml.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public interface Strings {

    String EMPTY = "";

    static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    static boolean notEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    static String resource(Class<?> clazz, String resource) {
        try (InputStream is = clazz.getResourceAsStream(resource)) {
            return null == is
                    ? Strings.EMPTY
                    : new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
