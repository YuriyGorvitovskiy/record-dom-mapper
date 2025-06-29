package io.openmapper.recordxml.config;

import java.lang.reflect.Type;

public non-sealed interface PolymorphicDescriptor<T> extends Descriptor {
    Iterable<Type> concreteTypes(Type genericType);

    Object toConcreteValue(Object value);

    T ofConcreteValue(Type declaredType, Object value);
}
