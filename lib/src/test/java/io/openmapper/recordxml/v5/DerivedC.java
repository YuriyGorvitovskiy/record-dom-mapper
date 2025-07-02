package io.openmapper.recordxml.v5;

import io.vavr.collection.Map;

public record DerivedC(String name, Base recursive, Map<String, Base> recursiveMap) implements Base {
}
