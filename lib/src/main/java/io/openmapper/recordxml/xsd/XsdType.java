package io.openmapper.recordxml.xsd;

public sealed interface XsdType permits XsdSimple, XsdComplex {
    XsdTypeRef ref();

}
