package io.openmapper.recordxml.xsd;

import io.openmapper.recordxml.xsd.XsdSimple.Predefined;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

public record XsdSchema(
        Map<String, XsdSimple> simple,
        Map<String, XsdComplex> complex,
        XsdElement root) {

    public static XsdSchema empty(String rootName, XsdTypeRef rootType) {
        return new XsdSchema(HashMap.empty(), HashMap.empty(), XsdElement.of(rootName, rootType));
    }

    public XsdSchema withRoot(XsdElement xsdElement) {
        return new XsdSchema(simple, complex, xsdElement);
    }

    public XsdSchema add(XsdType xsdType) {
        return switch (xsdType) {
            case XsdSimple simple -> this.add(simple);
            case XsdComplex complex -> this.add(complex);
        };
    }

    public XsdSchema add(XsdSimple xsdSimple) {
        if (xsdSimple instanceof Predefined) {
            return this;
        }
        return new XsdSchema(simple.put(xsdSimple.xsdName(), xsdSimple), complex, root);
    }

    public XsdSchema add(XsdComplex xsdComplex) {
        return new XsdSchema(simple, complex.put(xsdComplex.xsdType(), xsdComplex), root);
    }

}
