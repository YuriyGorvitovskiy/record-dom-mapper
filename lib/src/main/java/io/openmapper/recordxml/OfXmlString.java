package io.openmapper.recordxml;

@FunctionalInterface
public interface OfXmlString<T> {
    T ofXmlString(String xmlString);
}
