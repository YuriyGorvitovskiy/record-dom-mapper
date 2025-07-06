package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.xml.XmlPlainElement;
import io.vavr.collection.Seq;

public non-sealed interface SequenceMapper extends Mapper {
    Seq<XmlPlainElement> toXml(Object obj);
}
