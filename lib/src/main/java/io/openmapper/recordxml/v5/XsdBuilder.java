package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

import io.openmapper.recordxml.xsd.XsdSchema;
import io.openmapper.recordxml.xsd.XsdTypeRef;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

public record XsdBuilder(Config config) {

    public XsdSchema build(String name, Class<?> clazz) {
        java.util.Map<Type, MappingType> typeRefs = new java.util.HashMap<>();
        XsdResolver resolver = t -> typeRefs.computeIfAbsent(t, config::select);
        XsdTypeRef rootTypeRef = typeRefs.computeIfAbsent(clazz, config::select).xsdRef();

        XsdSchema schema = XsdSchema.empty(name, rootTypeRef);
        Set<Type> processed = HashSet.empty();
        while (processed.size() < typeRefs.size()) {
            Map<Type, MappingType> toProcess = HashMap.ofAll(typeRefs).removeAll(processed);
            processed = processed.addAll(toProcess.keySet());
            schema = toProcess.values()
                    .foldLeft(schema, (s, m) -> s.add(m.xsdType(resolver)));
        }

        return schema;
    }
}
