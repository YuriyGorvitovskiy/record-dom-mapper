package io.openmapper.recordxml.v5.xsd;

import io.vavr.collection.Seq;

public record sequence(Seq<element> element, String minOccurs, String maxOccurs) implements group {
}
