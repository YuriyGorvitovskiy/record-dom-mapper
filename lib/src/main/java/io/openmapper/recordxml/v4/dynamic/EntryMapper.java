package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.xml.XmlElement;

public interface EntryMapper<T> {

    T ofXml(XmlElement root);

    XmlElement toXml(Object value);
}
