package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.TypeName;
import io.openmapper.recordxml.v5.XmlNameSpace;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

public record complexExtensionType(String base, group group, @XmlNameSpace(SCHEMA) Seq<attribute> attribute) {
    public static complexExtensionType of(TypeName complexType, attribute... attributes) {
        return new complexExtensionType(complexType.name(), null, Array.of(attributes));
    }
}
