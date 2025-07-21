package io.openmapper.recordxml.v5;

import java.util.function.Supplier;

import io.openmapper.recordxml.v5.xsd.*;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public record XsdEntry<T>(T entry, Map<TypeName, Supplier<XsdEntry<complexType>>> types) {

    public static XsdEntry<TypeName> simple(String name) {
        return new XsdEntry<>(TypeName.of(name), HashMap.empty());
    }

    public static XsdEntry<TypeName> complex(XsdEntry<complexType> type) {
        TypeName typeName = TypeName.of(type.entry().name());
        return new XsdEntry<>(typeName, type.types().put(typeName, () -> type));
    }

    public static XsdEntry<TypeName> complex(TypeName name, Supplier<XsdEntry<complexType>> type) {
        return new XsdEntry<>(name, HashMap.of(name, type));
    }

    public static XsdEntry<choice> choice(Seq<XsdEntry<group>> choices, String minOccur, String maxOccur) {
        Map<TypeName, Supplier<XsdEntry<complexType>>> types = choices.foldLeft(HashMap.empty(), (m, c) -> m.merge(c.types));
        return new XsdEntry<>(new choice(choices.map(XsdEntry::entry), minOccur, maxOccur), types);
    }

    public static XsdEntry<group> elementSimple(String name, XsdEntry<TypeName> xsd) {
        return elementSimple(name, xsd, Occur.DEFAULT, Occur.DEFAULT);
    }

    public static XsdEntry<group> elementSimple(String name, XsdEntry<TypeName> xsd, String minOccur, String maxOccur) {
        return new XsdEntry<>(new element(name, xsd.entry.name(), minOccur, maxOccur), xsd.types);
    }

    public static XsdEntry<group> elementOfType(String name, XsdEntry<TypeName> xsd) {
        return new XsdEntry<>(new element(name, xsd.entry.name(), Occur.DEFAULT, Occur.DEFAULT), xsd.types);
    }

    public static XsdEntry<group> elementOfComplexType(String name, XsdEntry<complexType> xsd) {
        return new XsdEntry<>(
                new element(name, xsd.entry.name(), Occur.DEFAULT, Occur.DEFAULT),
                xsd.types.put(TypeName.of(xsd.entry().name()), () -> xsd));
    }

    public static XsdEntry<complexType> complexTypeWithSimpleContent(TypeName name, TypeName simpleType, attribute... attributes) {
        complexType type = complexType.ofSimpleContent(name, simpleType, attributes);
        return new XsdEntry<>(type, HashMap.empty());
    }

    public static XsdEntry<complexType> complexTypeWithComplexContent(TypeName name, XsdEntry<TypeName> entryXsd, attribute... attributes) {
        complexType type = complexType.ofComplexContent(name, entryXsd.entry(), attributes);
        return new XsdEntry<>(type, entryXsd.types());
    }

    public static XsdEntry<complexType> complexType(TypeName name, group group, attribute... attributes) {
        return complexType(name, group, Array.of(attributes));
    }

    public static XsdEntry<complexType> complexType(TypeName name, group group, Seq<attribute> attributes) {
        complexType type = complexType.of(name, group, attributes);
        return new XsdEntry<>(type, HashMap.empty());
    }

    public static XsdEntry<element> element(String name, TypeName type) {
        return new XsdEntry<>(element.of(name, type), HashMap.empty());
    }

    public XsdEntry<T> withTypes(Map<TypeName, Supplier<XsdEntry<complexType>>> types) {
        return new XsdEntry<>(entry, this.types.merge(types));
    }

    public XsdEntry<T> withType(complexType type) {
        XsdEntry<complexType> xsd = XsdEntry.complexType(type);
        return new XsdEntry<>(entry, this.types.put(TypeName.of(type.name()), () -> xsd));
    }

    public static XsdEntry<complexType> complexType(complexType type) {
        return new XsdEntry<>(type, HashMap.empty());
    }
}
