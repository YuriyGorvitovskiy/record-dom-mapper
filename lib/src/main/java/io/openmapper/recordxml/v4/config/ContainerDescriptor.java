package io.openmapper.recordxml.v4.config;

import java.lang.reflect.Type;

import io.vavr.collection.Seq;

public non-sealed interface ContainerDescriptor<T> extends Descriptor<T> {
    Type entryType(Type genericType);

    Iterable<?> entries(T value);

    T ofEntries(Type declaredType, Seq<?> entries);
}
