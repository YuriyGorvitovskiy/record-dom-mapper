package io.openmapper.recordxml.xsd;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record XsdComplex(XsdTypeRef ref,
                         Seq<XsdAttribute> attributes,
                         Seq<XsdElement> elements,
                         Option<XsdTypeRef> extendsType,
                         Bound bound) implements XsdType {
    public enum Bound {
        PLURAL,
        SINGLE
    }

    public static XsdComplex of(XsdTypeRef xsdTypeRef) {
        return new XsdComplex(xsdTypeRef, Array.empty(), Array.empty(), Option.none(), XsdComplex.Bound.SINGLE);
    }

    public XsdComplex addAttributes(Seq<XsdAttribute> attributes) {
        return new XsdComplex(ref, this.attributes.appendAll(attributes), elements, extendsType, bound);
    }

    public XsdComplex addElements(XsdElement... elements) {
        return addElements(Array.of(elements));
    }

    public XsdComplex addElements(Seq<XsdElement> elements) {
        return new XsdComplex(ref, attributes, this.elements.appendAll(elements), extendsType, bound);
    }

    public XsdComplex extendsType(XsdTypeRef extendsType) {
        return new XsdComplex(ref, attributes, elements, Option.of(extendsType), bound);
    }
}
