package io.openmapper.recordxml.v5;

import io.vavr.collection.Map;

public record RecursiveMap(String name,
                           Map<String, RecursiveMap> recursive) {
}
