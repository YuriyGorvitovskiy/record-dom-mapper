package io.openmapper.recordxml.v4.config;

import java.util.function.Predicate;

public record Selector<T>(Class<T> clazz, Match match) implements Predicate<Class<?>> {

    public enum Match {
        EXACT,
        SUPER,
    }

    @Override
    public boolean test(Class<?> clazz) {
        return switch (match) {
            case EXACT -> this.clazz == clazz;
            case SUPER -> this.clazz.isAssignableFrom(clazz);
        };
    }
}
