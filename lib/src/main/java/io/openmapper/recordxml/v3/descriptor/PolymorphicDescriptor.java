package io.openmapper.recordxml.v3.descriptor;

import java.lang.reflect.Type;

public non-sealed interface PolymorphicDescriptor<T> extends Descriptor<T> {
    Iterable<Type> concreteTypes(Type genericType);

    Object toConcreteValue(Object value);

    T ofConcreteValue(Type declaredType, Object value);
}
