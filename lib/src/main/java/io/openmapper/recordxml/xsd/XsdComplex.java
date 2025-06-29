package io.openmapper.recordxml.xsd;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

/**
 * extensionRef -> Option.none()
 * <p>
 * <xs:complexType name="ProductPrice">
 * <xs:choice maxOccurs="unbounded">
 * <xs:element name="ExtendedElement1" type="xs:decimal" />
 * <xs:element name="ExtendedElement2" type="xs:boolean" />
 * </xs:choice>
 * <xs:attribute name="currency" type="xs:string" use="required" />
 * <xs:attribute name="taxIncluded" type="xs:boolean" />
 * </xs:complexType>
 * <p>
 * extensionRef -> simple Type
 * <p>
 * <xs:complexType name="ProductPrice">
 * <xs:simpleContent>
 * <xs:extension base="xs:decimal">
 * <xs:attribute name="currency" type="xs:string" use="required" />
 * <xs:attribute name="taxIncluded" type="xs:boolean" />
 * </xs:extension>
 * </xs:simpleContent>
 * </xs:complexType>
 * <p>
 * extensionRef -> complex Type
 * <p>
 * <xs:complexType name="ExtendedType">
 * <xs:complexContent>
 * <xs:extension base="BaseType">
 * <xs:choice maxOccurs="unbounded">
 * <xs:element name="ExtendedElement1" type="xs:decimal" />
 * <xs:element name="ExtendedElement2" type="xs:boolean" />
 * </xs:choice>
 * <xs:attribute name="currency" type="xs:string" use="required" />
 * <xs:attribute name="taxIncluded" type="xs:boolean" />
 * </xs:extension>
 * </xs:complexContent>
 * </xs:complexType>
 */

public record XsdComplex(XsdTypeRef ref,
                         Option<XsdTypeRef> extensionRef,
                         Seq<XsdAttribute> attributes,
                         Seq<XsdElement> elements) implements XsdType {

    public static XsdComplex of(XsdTypeRef xsdTypeRef) {
        return new XsdComplex(xsdTypeRef, Option.none(), Array.empty(), Array.empty());
    }

    public XsdComplex addAttributes(XsdAttribute... attributes) {
        return addAttributes(Array.of(attributes));
    }

    public XsdComplex addAttributes(Seq<XsdAttribute> attributes) {
        return new XsdComplex(ref, extensionRef, this.attributes.appendAll(attributes), elements);
    }

    public XsdComplex addElements(XsdElement... elements) {
        return addElements(Array.of(elements));
    }

    public XsdComplex addElements(Seq<XsdElement> elements) {
        return new XsdComplex(ref, extensionRef, attributes, this.elements.appendAll(elements));
    }

    public XsdComplex mergeType(XsdComplex mergeWith) {
        return new XsdComplex(ref, extensionRef, attributes.appendAll(mergeWith.attributes), elements.appendAll(mergeWith.elements));
    }

    public XsdComplex extensionOf(XsdTypeRef extensionRef) {
        return new XsdComplex(ref, Option.of(extensionRef), attributes, elements);
    }
}
