package io.openmapper.recordxml.xsd;

/**
 * bound -> DEFAULT
 * <xs:element name="ExtendedElement1" type="xs:decimal"/>
 * <p>
 * bound -> OPTIONAL
 * <xs:element name="ExtendedElement1" type="xs:decimal" minOccurs="0" maxOccurs="1"/>
 * <p>
 * bound -> SINGLE
 * <xs:element name="ExtendedElement1" type="xs:decimal" minOccurs="1" maxOccurs="1"/>
 * <p>
 * bound -> OPTIONAL
 * <xs:element name="ExtendedElement1" type="xs:decimal" minOccurs="0" maxOccurs="unbounded"/>
 */
public record XsdElement(String name, XsdTypeRef type, XsdBound bound) {

    public static XsdElement of(String name, XsdTypeRef type) {
        return new XsdElement(name, type, XsdBound.DEFAULT);
    }

    public XsdElement withType(XsdTypeRef type) {
        return new XsdElement(name, type, bound);
    }
}
