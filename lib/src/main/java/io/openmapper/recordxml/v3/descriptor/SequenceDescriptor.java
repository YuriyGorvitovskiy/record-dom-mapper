package io.openmapper.recordxml.v3.descriptor;

import java.lang.reflect.Type;

import io.vavr.collection.Seq;

public non-sealed interface SequenceDescriptor<T> extends Descriptor<T> {
    Type entryType(Type genericType);

    Iterable<?> entries(T value);

    T ofEntries(Type declaredType, Seq<?> entries);
}
