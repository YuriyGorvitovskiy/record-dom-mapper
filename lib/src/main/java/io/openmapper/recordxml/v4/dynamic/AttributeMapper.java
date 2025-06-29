package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.v2.Mapper;
import io.openmapper.recordxml.xml.XmlAttribute;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.control.Option;

public record AttributeMapper<P, E>(
        String name,
        PresenceMapper<P, E> presence,
        PrimitiveMapper<E> primitive) implements Mapper {


    @Override
    public Iterable<XmlUnit> toXml(Object value) {
        Option<E> element = presence.toOption((P) value);
        return element.map(e -> XmlAttribute.of(name, primitive.toXmlString(e)));
    }

    @Override
    public Object ofXml(XmlElement parent) {
        Option<E> element = parent.attributes().get(name)
                .map(XmlAttribute::value)
                .map(primitive::ofXmlString);

        return presence.ofOption(element);
    }
}
