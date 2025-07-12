package io.openmapper.recordxml.v5.xsd;

import io.vavr.collection.Seq;

public record complexType(String name, group group, Seq<attribute> attribute) {
}
