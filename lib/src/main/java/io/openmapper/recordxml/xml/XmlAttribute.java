package io.openmapper.recordxml.xml;


public record XmlAttribute(String name, String value) implements XmlUnit {
    public static XmlAttribute of(String name, String value) {
        return new XmlAttribute(name, value);
    }
}
