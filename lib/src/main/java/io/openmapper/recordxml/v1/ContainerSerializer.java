package io.openmapper.recordxml.v1;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;

@FunctionalInterface
public interface ContainerSerializer {
    Option<Traversable<?>> getEntries(Object container);
}




