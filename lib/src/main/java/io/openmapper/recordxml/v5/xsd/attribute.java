package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.TypeName;

public record attribute(String name, String type) {
    public static attribute of(String name, TypeName type) {
        return new attribute(name, type.name());
    }
}
