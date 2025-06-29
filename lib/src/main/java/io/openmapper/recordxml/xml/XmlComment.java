package io.openmapper.recordxml.xml;

import org.w3c.dom.Document;

public record XmlComment(String value) implements XmlNode {
    public static XmlComment of(String value) {
        return new XmlComment(value);
    }

    @Override
    public org.w3c.dom.Comment toDOM(Document document) {
        return document.createComment(value);
    }
}
