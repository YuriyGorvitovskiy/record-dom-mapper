package io.openmapper.recordxml.v5;

import io.vavr.collection.Map;

public record Simple(String name,
                     Map<String, String> map) {
}
