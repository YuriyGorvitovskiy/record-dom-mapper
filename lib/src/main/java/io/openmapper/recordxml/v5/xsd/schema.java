package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.XmlNameSpace;
import io.vavr.collection.Seq;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;
import static io.openmapper.recordxml.v5.XmlNameSpace.XMLNS;

public record schema(@XmlNameSpace(XMLNS) String xs,
                     @XmlNameSpace(SCHEMA) element element,
                     @XmlNameSpace(SCHEMA) Seq<complexType> complexType) {

    public static schema of(element element, Seq<complexType> complexType) {
        return new schema("http://www.w3.org/2001/XMLSchema", element, complexType);
    }
}
