package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record FieldPolymorphicMapper<S, E>(
        String fieldName,
        SequenceMapper<S, E> sequence,
        PolymorphicMapper<E> polymorphicMapper) implements Mapper<S> {

    @Override
    public Option<XmlElement> toXml(Object value) {
        Seq<XmlElement> elements = sequence.toXml(
                polymorphicMapper::toXml,
                (S) value);

        return elements.isEmpty()
                ? Option.none()
                : Option.of(XmlElement.of(fieldName).withChildren(elements));
    }

    @Override
    public S ofXml(XmlElement parent) {
        Option<XmlElement> field = parent.children()
                .find(n -> n instanceof XmlElement e && e.name().equals(fieldName))
                .map(n -> (XmlElement) n);

        return sequence.ofXml(
                field.getOrElse(() -> XmlElement.of(fieldName)),
                n -> true,
                polymorphicMapper::ofXml
        );
    }
}
