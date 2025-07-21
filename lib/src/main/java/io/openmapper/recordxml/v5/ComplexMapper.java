package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.vavr.control.Option;

public non-sealed interface ComplexMapper extends Mapper {

    Option<XmlPlainElement> toXml(Object obj);

    Object ofXml(XmlElement xml);

    XsdEntry<TypeName> xsd();
}
