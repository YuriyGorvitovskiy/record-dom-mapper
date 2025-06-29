package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

import io.openmapper.recordxml.xsd.XsdSchema;
import io.openmapper.recordxml.xsd.XsdTypeRef;
import io.vavr.collection.Iterator;

public record XsdBuilder(Config config) {

    public XsdSchema build(String name, Class<?> clazz) {
        java.util.Map<Type, MappingType> typeRefs = new java.util.HashMap<>();
        XsdResolver resolver = t -> typeRefs.computeIfAbsent(t, config::select);
        XsdTypeRef rootType = typeRefs.computeIfAbsent(clazz, config::select).xsdRef();

        XsdSchema schema = Iterator.ofAll(typeRefs.values()).foldLeft(XsdSchema.empty(name, rootType),
                (s, m) -> s.add(m.xsdType(resolver)));

        return schema;
    }
}
