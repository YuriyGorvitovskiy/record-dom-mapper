package io.openmapper.recordxml.config.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

import io.openmapper.recordxml.config.ContainerDescriptor;
import io.openmapper.recordxml.config.Selector;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.openmapper.recordxml.xsd.XsdComplex;
import io.vavr.collection.Seq;

public enum VavrMapDescriptor implements ContainerDescriptor {

    MAP;

    @Override
    public Type entryType(Type genericType) {
        return ((ParameterizedType) genericType).getActualTypeArguments()[1];
    }

    @Override
    public Seq<XmlUnit> entries(Object value) {
        return null;
    }

    @Override
    public XsdComplex xsd(Type declaredType) {
        return new XsdComplex(MapOf);
    }

    @Override
    public Object ofEntries(Type declaredType, XmlElement parent, Function<XmlElement, Object> elementParser) {
        return null;
    }

    @Override
    public Selector selector() {
        return null;
    }
}
