package io.openmapper.recordxml.xsd;

public record XsdAttribute(String name, XsdTypeRef xsdTypeName) {
    public static XsdAttribute of(String name, XsdTypeRef xsdTypeName) {
        return new XsdAttribute(name, xsdTypeName);
    }
}
