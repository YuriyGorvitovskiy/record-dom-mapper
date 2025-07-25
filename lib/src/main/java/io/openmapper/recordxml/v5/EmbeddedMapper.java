package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.v5.xsd.complexType;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public non-sealed interface EmbeddedMapper extends Mapper {
    Set<String> names();

    Option<XmlPlainElement> toXml(Object obj);

    Object ofXml(XmlElement xml);

    XsdEntry<complexType> xsd();

}
