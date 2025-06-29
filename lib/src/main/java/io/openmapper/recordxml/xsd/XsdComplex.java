package io.openmapper.recordxml.xsd;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;

public record XsdComplex(XsdTypeRef ref,
                         Seq<XsdAttribute> attributes,
                         Seq<XsdElement> elements,
                         Bound bound) implements XsdType {
    public enum Bound {
        PLURAL,
        SINGLE
    }

    public static XsdComplex of(XsdTypeRef xsdTypeRef) {
        return new XsdComplex(xsdTypeRef, Array.empty(), Array.empty(), XsdComplex.Bound.SINGLE);
    }

    public XsdComplex addAttributes(XsdAttribute... attributes) {
        return addAttributes(Array.of(attributes));
    }

    public XsdComplex addAttributes(Seq<XsdAttribute> attributes) {
        return new XsdComplex(ref, this.attributes.appendAll(attributes), elements, bound);
    }

    public XsdComplex addElements(XsdElement... elements) {
        return addElements(Array.of(elements));
    }

    public XsdComplex addElements(Seq<XsdElement> elements) {
        return new XsdComplex(ref, attributes, this.elements.appendAll(elements), bound);
    }

    public XsdComplex mergeType(XsdComplex mergeWith) {
        return new XsdComplex(ref, attributes.appendAll(mergeWith.attributes), elements.appendAll(mergeWith.elements), bound);
    }


}
