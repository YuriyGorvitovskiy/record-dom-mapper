package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.XmlNameSpace;
import io.vavr.collection.Seq;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

@XmlNameSpace(SCHEMA)
public record sequence(Seq<group> groups, String minOccurs, String maxOccurs) implements group {
    public static sequence of(Seq<group> groups) {
        return new sequence(groups, Occur.DEFAULT, Occur.DEFAULT);
    }
}
