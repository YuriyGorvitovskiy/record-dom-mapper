package io.openmapper.recordxml.v2;

@FunctionalInterface
public interface ToXmlString<T> {
    String toXmlString(T value);
}
