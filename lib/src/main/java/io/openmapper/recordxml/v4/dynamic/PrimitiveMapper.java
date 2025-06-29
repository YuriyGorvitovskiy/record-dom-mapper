package io.openmapper.recordxml.v4.dynamic;

public interface PrimitiveMapper<P> {

    String toXmlString(P value);

    P ofXmlString(String xmlString);
}
