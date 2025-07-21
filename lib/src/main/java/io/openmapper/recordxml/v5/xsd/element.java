package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.TypeName;
import io.openmapper.recordxml.v5.XmlNameSpace;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

@XmlNameSpace(SCHEMA)
public record element(String name, String type, String minOccurs, String maxOccurs) implements group {

    public static element of(String name, TypeName type) {
        return new element(name, type.name(), Occur.DEFAULT, Occur.DEFAULT);
    }

    public static element of(String name, TypeName type, String minOccurs, String maxOccurs) {
        return new element(name, type.name(), minOccurs, maxOccurs);
    }
}
