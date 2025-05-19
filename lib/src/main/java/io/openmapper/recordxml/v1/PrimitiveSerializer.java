package io.openmapper.recordxml.v1;

import io.vavr.control.Option;


@FunctionalInterface
public interface PrimitiveSerializer {
    Option<String> serialize(Object value);
}


