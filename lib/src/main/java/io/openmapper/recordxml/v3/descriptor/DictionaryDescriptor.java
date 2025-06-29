package io.openmapper.recordxml.v3.descriptor;

import java.lang.reflect.Type;

import io.vavr.Tuple2;
import io.vavr.collection.Traversable;

public non-sealed interface DictionaryDescriptor<T> extends Descriptor<T> {

    Type keyType(Type genericType);

    Type entryType(Type genericType);

    Iterable<Tuple2<?, ?>> entries(T value);

    T ofKeyEntries(Type declaredType, Traversable<Tuple2<?, ?>> entries);
}
