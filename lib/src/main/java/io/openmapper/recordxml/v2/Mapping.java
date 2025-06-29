package io.openmapper.recordxml.v2;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;

public interface Mapping {

    Iterable<XmlUnit> toXml(Object value);

    Object ofXml(XmlElement parent);
}
