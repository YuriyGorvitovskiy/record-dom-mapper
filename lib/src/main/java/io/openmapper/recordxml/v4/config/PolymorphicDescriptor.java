package io.openmapper.recordxml.v4.config;

import java.lang.reflect.Type;

public non-sealed interface PolymorphicDescriptor<T> extends Descriptor<T> {
    Iterable<Type> concreteTypes(Type genericType);

    Object toConcreteValue(Object value);

    T ofConcreteValue(Type declaredType, Object value);
}
