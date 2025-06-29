package io.openmapper.recordxml.xsd;

import io.openmapper.recordxml.xsd.XsdSimple.Predefined;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public record XsdSchema(
        Map<XsdTypeRef, XsdSimple> simple,
        Map<XsdTypeRef, XsdComplex> complex,
        XsdElement root) {

    public static XsdSchema empty(String rootName, XsdTypeRef rootType) {
        return new XsdSchema(HashMap.empty(), HashMap.empty(), XsdElement.of(rootName, rootType));
    }

    public XsdSchema add(XsdType xsdType) {
        return switch (xsdType) {
            case XsdSimple s -> this.add(s);
            case XsdComplex c -> this.add(c);
        };
    }

    public XsdSchema add(XsdSimple xsdSimple) {
        if (xsdSimple instanceof Predefined) {
            return this;
        }
        return new XsdSchema(simple.put(xsdSimple.ref(), xsdSimple), complex, root);
    }

    public XsdSchema add(XsdComplex xsdComplex) {
        return new XsdSchema(simple, complex.put(xsdComplex.ref(), xsdComplex), root);
    }

    public XsdSchema add(Seq<? extends XsdType> xsdTypes) {
        var simpleAndComplex = xsdTypes.partition(XsdSimple.class::isInstance);
        return new XsdSchema(
                simpleAndComplex._1
                        .filter(s -> !(s instanceof Predefined))
                        .toMap(XsdType::ref, s -> (XsdSimple) s)
                        .merge(simple),
                simpleAndComplex._2
                        .toMap(XsdType::ref, s -> (XsdComplex) s)
                        .merge(complex),
                root);
    }

}
