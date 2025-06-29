package io.openmapper.recordxml.config;

import java.util.function.Predicate;

public record Selector(Class<?> clazz, Match match) implements Predicate<Class<?>> {

    public enum Match {
        EXACT,
        BASE,
    }
    
    public static Selector exact(Class<?> clazz) {
        return new Selector(clazz, Match.EXACT);
    }

    public static Selector base(Class<?> clazz) {
        return new Selector(clazz, Match.BASE);
    }

    @Override
    public boolean test(Class<?> clazz) {
        return switch (match) {
            case EXACT -> this.clazz == clazz;
            case BASE -> this.clazz.isAssignableFrom(clazz);
        };
    }
}
