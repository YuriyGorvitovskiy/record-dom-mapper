package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Array;

public record XmlReader(Config config) {

    @SuppressWarnings("unchecked")
    public <T> T ofXml(Class<T> clazz, XmlElement root) {
        java.util.Map<Type, MappingType> typeRefs = new java.util.HashMap<>();
        MappingResolver resolver = t -> typeRefs.computeIfAbsent(t, config::select);

        MappingType mapping = typeRefs.computeIfAbsent(clazz, config::select);
        if (mapping.isPolymorphic()) {
            return (T) mapping.ofXml(resolver, root, root.elements());
        }
        if (mapping.isSimple()) {
            return (T) mapping.ofXml(root.text());
        }
        return (T) mapping.ofXml(resolver, null, Array.of(root));
    }
}
