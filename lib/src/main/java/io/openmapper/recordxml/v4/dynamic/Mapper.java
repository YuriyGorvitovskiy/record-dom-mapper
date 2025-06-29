package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;

public interface Mapper<T> {

    Iterable<? extends XmlUnit> toXml(T value);

    T ofXml(XmlElement parent);

}
