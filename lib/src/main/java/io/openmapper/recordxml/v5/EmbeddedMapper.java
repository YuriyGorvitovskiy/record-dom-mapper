package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.xml.XmlPlainElement;
import io.vavr.control.Option;

public non-sealed interface EmbeddedMapper extends Mapper {
    Option<XmlPlainElement> toXml(Object obj);
}
