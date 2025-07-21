package io.openmapper.recordxml.v5;

public non-sealed interface SimpleMapper extends Mapper {

    String toXml(Object obj);

    Object ofXml(String xml);

    XsdEntry<TypeName> xsd();
}
