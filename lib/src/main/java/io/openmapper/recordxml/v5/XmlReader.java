package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Array;

public record XmlReader(Config config) {

    @SuppressWarnings("unchecked")
    public <T> T ofXml(Class<T> clazz, XmlElement root) {
        java.util.Map<Type, MappingType> typeRefs = new java.util.HashMap<>();
        XsdResolver resolver = t -> typeRefs.computeIfAbsent(t, config::select);

        MappingType mapping = typeRefs.computeIfAbsent(clazz, config::select);
        return mapping.isSimple()
                ? (T) mapping.ofXml(root.text())
                : (T) mapping.ofXml(resolver, root, Array.empty());
    }
}
