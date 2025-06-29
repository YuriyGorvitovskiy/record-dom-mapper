package io.openmapper.recordxml.config;

import java.lang.reflect.Type;

import io.vavr.collection.Seq;

public non-sealed interface ComplexDescriptor extends Descriptor {
    interface Field {
        String xmlName();

        Type genericType();
    }

    String xmlName();

    String xsdType();

    Iterable<Field> fields();

    Object fieldValue(Object from, Field field);

    Object ofFields(Type declaredType, Seq<?> fieldsValues);

}
