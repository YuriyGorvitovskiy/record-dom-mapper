package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public non-sealed interface ChoiceMapper extends Mapper {

    Set<String> names();

    Option<XmlElement> toXml(Object value);

    Object ofXml(XmlElement xmlElement);
}
