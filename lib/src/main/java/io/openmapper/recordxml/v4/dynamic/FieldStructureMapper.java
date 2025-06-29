package io.openmapper.recordxml.v4.dynamic;

import java.util.function.Function;
import java.util.function.Supplier;

import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Seq;

public record FieldStructureMapper<S, E>(
        String fieldName,
        SequenceMapper<S, E> sequence,
        Supplier<StructureMapper<E>> structureMapper) implements Mapper<S> {

    @Override
    public Seq<XmlElement> toXml(Object value) {
        Function<E, XmlElement> mapper = (e) -> structureMapper.get().toXml(fieldName, e);
        return sequence.toXml(
                mapper,
                (S) value);
    }

    @Override
    public S ofXml(XmlElement parent) {
        return sequence.ofXml(
                parent,
                n -> fieldName.equals(n),
                structureMapper.get()::ofXml
        );
    }
}
