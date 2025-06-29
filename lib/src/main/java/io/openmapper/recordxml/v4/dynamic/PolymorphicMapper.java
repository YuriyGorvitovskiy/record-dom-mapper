package io.openmapper.recordxml.v4.dynamic;

import java.util.function.Supplier;

import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Map;

public record PolymorphicMapper<P>(Map<String, Supplier<StructureMapper<P>>> structuresByXmlName,
                                   Map<Class<?>, Supplier<StructureMapper<P>>> structuresByRawClass,
                                   Map<Class<?>, String> nameByRawClass) {

    public XmlElement toXml(P value) {
        Class<?> clazz = value.getClass();

        StructureMapper mapper = structuresByRawClass.get(clazz)
                .getOrElseThrow(() -> new IllegalArgumentException("No mapper for class " + clazz))
                .get();

        return mapper.toXml(
                nameByRawClass
                        .get(clazz)
                        .getOrElseThrow(() -> new IllegalArgumentException("No name for class " + clazz)),
                value);
    }

    public P ofXml(XmlElement xmlElement) {
        return structuresByXmlName.get(xmlElement.name())
                .getOrElseThrow(() -> new IllegalArgumentException("No mapper for xml element " + xmlElement.name()))
                .get()
                .ofXml(xmlElement);
    }

}
