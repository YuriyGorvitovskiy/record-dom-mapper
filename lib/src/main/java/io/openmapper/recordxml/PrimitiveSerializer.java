package io.openmapper.recordxml;

import io.vavr.control.Option;


@FunctionalInterface
public interface PrimitiveSerializer {
    Option<String> serialize(Object value);
}


