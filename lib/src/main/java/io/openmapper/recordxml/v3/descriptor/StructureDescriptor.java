package io.openmapper.recordxml.v3.descriptor;

import java.lang.reflect.Type;

import io.vavr.collection.Seq;

public non-sealed interface StructureDescriptor<T> extends Descriptor<T> {
    interface Field {
        String xmlName();

        Type genericType();
    }

    String xmlName();

    Iterable<Field> fields();

    Object fieldValue(T from, Field field);

    T ofFields(Type declaredType, Seq<?> fieldsValues);
}
