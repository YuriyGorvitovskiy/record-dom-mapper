package io.openmapper.recordxml.config.impl;

import java.lang.reflect.Type;
import java.util.function.Function;

import io.openmapper.recordxml.config.Selector;
import io.openmapper.recordxml.config.SimpleDescriptor;
import io.openmapper.recordxml.xsd.XsdSimple;
import io.openmapper.recordxml.xsd.XsdSimple.Predefined;

public enum SimplePredefinedDescriptor implements SimpleDescriptor {

    STRING(String.class, Predefined.STRING, s -> s, s -> s),
    INTEGER(Integer.class, Predefined.INTEGER, Object::toString, Integer::parseInt),
    LONG(Long.class, Predefined.LONG, Object::toString, Long::parseLong),
    FLOAT(Float.class, Predefined.FLOAT, Object::toString, Float::parseFloat),
    DOUBLE(Double.class, Predefined.DOUBLE, Object::toString, Double::parseDouble);

    private final Selector selector;
    private final XsdSimple xsdType;
    private final Function<Object, String> toXml;
    private final Function<String, Object> ofXml;

    @SuppressWarnings({"unchecked", "rawtypes"})
    <T> SimplePredefinedDescriptor(Class<T> clazz, XsdSimple xsdType, Function<T, String> toXml, Function<String, T> ofXml) {
        this.selector = Selector.exact(clazz);
        this.xsdType = xsdType;
        this.toXml = (Function) toXml;
        this.ofXml = (Function) ofXml;
    }

    @Override
    public String toXml(Object value) {
        return toXml.apply(value);
    }

    @Override
    public Object ofXml(Type declaredType, String xmlString) {
        return ofXml.apply(xmlString);
    }

    @Override
    public XsdSimple xsdType() {
        return xsdType;
    }

    @Override
    public Selector selector() {
        return selector;
    }
}
