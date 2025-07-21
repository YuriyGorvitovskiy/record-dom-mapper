package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.XmlNameSpace;
import io.vavr.collection.Seq;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

@XmlNameSpace(SCHEMA)
public record choice(
        Seq<group> groups,
        String minOccurs,
        String maxOccurs) implements group {

    public static choice of(Seq<group> groups, String minOccurs, String maxOccurs) {
        return new choice(groups, minOccurs, maxOccurs);
    }

    public choice withOccurs(String minOccurs, String maxOccurs) {
        return new choice(groups, minOccurs, maxOccurs);
    }
}
