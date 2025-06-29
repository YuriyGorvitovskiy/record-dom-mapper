package io.openmapper.recordxml.xsd;

public record XsdTypeRef(String name) {
    public static XsdTypeRef of(String xsdName) {
        return new XsdTypeRef(xsdName);
    }

    public String nameWithoutNamespace() {
        return name.substring(name.indexOf(':') + 1);
    }
}
