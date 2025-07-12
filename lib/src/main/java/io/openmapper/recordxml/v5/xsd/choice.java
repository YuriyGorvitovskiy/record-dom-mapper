package io.openmapper.recordxml.v5.xsd;

import io.vavr.collection.Seq;

public record choice(Seq<group> groups, String minOccurs, String maxOccurs) implements group {
}
