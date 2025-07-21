package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.TypeName;
import io.openmapper.recordxml.v5.XmlNameSpace;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

public record complexType(String name,
                          particle particle,
                          @XmlNameSpace(SCHEMA) Seq<attribute> attribute) {

    public static complexType ofSimpleContent(TypeName name, TypeName simpleType, attribute... attributes) {
        return new complexType(
                name.name(),
                simpleContent.of(simpleType, attributes),
                Array.empty());
    }

    public static complexType ofComplexContent(TypeName name, TypeName complexType, attribute... attributes) {
        return new complexType(
                name.name(),
                complexContent.of(complexType, attributes),
                Array.empty());
    }

    public static complexType of(TypeName name, group group, attribute... attributes) {
        return of(name, group, Array.of(attributes));
    }

    public static complexType of(TypeName name, group group, Seq<attribute> attributes) {
        return new complexType(name.name(), group, attributes);
    }

    public group group() {
        return switch (particle) {
            case group g -> g;
            case complexContent c -> c.extension().group();
            case simpleContent ignore -> null;
            case null -> null;
        };
    }

    public Seq<attribute> attributes() {
        return switch (particle) {
            case complexContent c -> c.extension().attribute();
            case simpleContent s -> s.extension().attribute();
            case null, default -> attribute();
        };
    }
}
