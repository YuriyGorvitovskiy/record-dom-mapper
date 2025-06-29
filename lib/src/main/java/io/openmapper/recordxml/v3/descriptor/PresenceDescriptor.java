package io.openmapper.recordxml.v3.descriptor;

import java.lang.reflect.Type;

public non-sealed interface PresenceDescriptor<T> extends Descriptor<T> {
    Type entryType(Type genericType);

    boolean isEmpty(T presence);

    T empty();

    Object entry(T presence);

    T ofEntry(Type declaredType, Object value);
}
