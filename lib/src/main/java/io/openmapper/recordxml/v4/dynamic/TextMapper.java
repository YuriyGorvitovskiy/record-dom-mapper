package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlText;
import io.openmapper.recordxml.xml.XmlText.Format;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record TextMapper<P, E>(
        Format format,
        PresenceMapper<P, E> presence,
        PrimitiveMapper<E> primitive) implements Mapper {

    @Override
    public Option<XmlText> toXml(Object value) {
        Option<E> element = presence.toOption((P) value);
        return element.map(e -> XmlText.of(format, primitive.toXmlString(e)));
    }

    @Override
    public Object ofXml(XmlElement parent) {
        Seq<String> texts = parent.children()
                .filter(e -> e instanceof XmlText)
                .map(e -> ((XmlText) e).value());

        return presence.ofOption(texts.isEmpty()
                ? Option.none()
                : Option.of(primitive.ofXmlString(texts.mkString().trim())));
    }
}
