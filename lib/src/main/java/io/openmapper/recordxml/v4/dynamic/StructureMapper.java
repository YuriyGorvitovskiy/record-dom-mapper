package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.xml.XmlElement;

public interface StructureMapper<S> {

    XmlElement toXml(String name, S object);

    S ofXml(XmlElement xmlElement);
}
