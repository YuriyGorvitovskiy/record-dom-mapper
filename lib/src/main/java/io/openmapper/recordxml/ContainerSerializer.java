package io.openmapper.recordxml;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;

@FunctionalInterface
public interface ContainerSerializer {
    Option<Traversable<?>> getEntries(Object container);
}




