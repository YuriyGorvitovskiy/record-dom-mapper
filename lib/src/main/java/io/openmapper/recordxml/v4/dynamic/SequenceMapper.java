package io.openmapper.recordxml.v4.dynamic;

import java.util.function.Function;
import java.util.function.Predicate;

import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Seq;

public interface SequenceMapper<S, E> {

    Seq<XmlElement> toXml(Function<E, XmlElement> elementToXml, S value);

    S ofXml(XmlElement parent, Predicate<String> nameFilter, Function<XmlElement, E> elementOfXml);
}
