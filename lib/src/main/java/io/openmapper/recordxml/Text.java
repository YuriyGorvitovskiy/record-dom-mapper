package io.openmapper.recordxml;

import org.w3c.dom.Document;

public record Text(Format format, String value) implements Node {

    public enum Format {
        TEXT,
        CDATA
    }

    public static Text of(String value) {
        return of(Format.TEXT, value);
    }

    public static Text of(Format format, String value) {
        return new Text(format, value);
    }


    @Override
    public org.w3c.dom.Text toDOM(Document document) {
        return switch (format) {
            case TEXT -> document.createTextNode(value);
            case CDATA -> document.createCDATASection(value);
        };
    }

}
