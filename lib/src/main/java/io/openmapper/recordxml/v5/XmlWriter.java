package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlText;
import io.vavr.control.Option;

public record XmlWriter(Config config) {


    public XmlElement toXml(String rootName, Object value) {
        XmlElement root = XmlElement.of(rootName);
        if (value == null) {
            return root;
        }
        java.util.Map<Type, MappingType> typeRefs = new java.util.HashMap<>();
        XsdResolver resolver = t -> typeRefs.computeIfAbsent(t, config::select);

        MappingType mapping = typeRefs.computeIfAbsent(value.getClass(), config::select);
        if (mapping.isSimple()) {
            return root.withChildren(XmlText.of(mapping.toXml(value)));
        }
        Option<XmlElement> mapped = mapping.toXml(resolver, rootName, value)
                .find(e -> e instanceof XmlElement)
                .map(e -> (XmlElement) e);
        return mapped.getOrElse(root);
    }
}
