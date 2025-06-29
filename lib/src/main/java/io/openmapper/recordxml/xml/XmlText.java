package io.openmapper.recordxml.xml;

import org.w3c.dom.Document;

public record XmlText(Format format, String value) implements XmlNode {

    public enum Format {
        TEXT,
        CDATA
    }

    public static XmlText of(String value) {
        return of(Format.TEXT, value);
    }

    public static XmlText of(Format format, String value) {
        return new XmlText(format, value);
    }


    @Override
    public org.w3c.dom.Text toDOM(Document document) {
        return switch (format) {
            case TEXT -> document.createTextNode(value);
            case CDATA -> document.createCDATASection(value);
        };
    }

}
