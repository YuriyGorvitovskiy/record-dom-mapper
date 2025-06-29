package io.openmapper.recordxml.xsd;

public record XsdElement(String name, XsdTypeRef type) {

    public static XsdElement of(String name, XsdTypeRef type) {
        return new XsdElement(name, type);
    }
}
