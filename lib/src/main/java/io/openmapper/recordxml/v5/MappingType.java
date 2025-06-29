package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.xsd.XsdType;
import io.openmapper.recordxml.xsd.XsdTypeRef;

public interface MappingType {

    boolean isSimple();

    boolean isPolymorphic();

    XsdTypeRef xsdRef();

    XsdType xsdType(XsdResolver resolver);
}
