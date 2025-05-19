package io.openmapper.recordxml;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;

public record Mapper() {

    static final Mapper STOCK = new Mapper();

    public static Mapper stock() {
        return STOCK;
    }

    public <T> Document recordToXml(DocumentBuilder documentBuilder, T ignoredRecord) {
        return documentBuilder.newDocument();
    }

    public <T> T xmlToRecord(Class<T> ignoredRecordClass, Document ignoredDocument) {
        return null;
    }
}
