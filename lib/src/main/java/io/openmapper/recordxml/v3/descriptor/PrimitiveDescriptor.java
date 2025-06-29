package io.openmapper.recordxml.v3.descriptor;

import java.lang.reflect.Type;

public non-sealed interface PrimitiveDescriptor<T> extends Descriptor<T> {
    String toXml(T value);

    T ofXml(Type declaredType, String xmlString);
}
