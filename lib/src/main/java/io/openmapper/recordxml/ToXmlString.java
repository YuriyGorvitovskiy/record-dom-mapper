package io.openmapper.recordxml;

@FunctionalInterface
public interface ToXmlString<T> {
    String toXmlString(T value);
}
