package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.TypeName;
import io.openmapper.recordxml.v5.XmlNameSpace;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

public record simpleExtensionType(String base, @XmlNameSpace(SCHEMA) Seq<attribute> attribute) {
    public static simpleExtensionType of(TypeName simpleType, attribute... attributes) {
        return new simpleExtensionType(simpleType.name(), Array.of(attributes));
    }
}
