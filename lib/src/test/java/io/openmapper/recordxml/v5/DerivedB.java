package io.openmapper.recordxml.v5;

import io.vavr.collection.Map;

public record DerivedB(String name, Map<String, Base> recursiveMap) implements Base {
}
