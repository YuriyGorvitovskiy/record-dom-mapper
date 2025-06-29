package io.openmapper.recordxml.config;

import java.lang.reflect.Type;

import io.openmapper.recordxml.xsd.XsdSimple;

public non-sealed interface SimpleDescriptor extends Descriptor {
    String toXml(Object value);

    Object ofXml(Type declaredType, String xmlString);

    XsdSimple xsdType();
}
