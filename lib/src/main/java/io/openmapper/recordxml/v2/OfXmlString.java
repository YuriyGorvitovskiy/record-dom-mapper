package io.openmapper.recordxml.v2;

@FunctionalInterface
public interface OfXmlString<T> {
    T ofXmlString(String xmlString);
}
