package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlText;
import io.openmapper.recordxml.xml.XmlText.Format;
import io.vavr.collection.Seq;

public record FieldTextMapper<S, E>(
        String fieldName,
        Format format,
        SequenceMapper<S, E> sequence,
        PrimitiveMapper<E> primitive) implements Mapper<S> {

    @Override
    public Seq<XmlElement> toXml(Object value) {
        return sequence.toXml(
                e -> XmlElement.of(fieldName).withChildren(XmlText.of(format, primitive.toXmlString(e))),
                (S) value);
    }

    @Override
    public S ofXml(XmlElement parent) {
        return sequence.ofXml(
                parent, n -> fieldName.equals(n),
                e -> primitive.ofXmlString(e.text())
        );
    }

}
