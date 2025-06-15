package io.openmapper.recordxml;

import org.w3c.dom.Document;

public record Comment(String value) implements Node {
    public static Comment of(String value) {
        return new Comment(value);
    }

    @Override
    public org.w3c.dom.Comment toDOM(Document document) {
        return document.createComment(value);
    }
}
