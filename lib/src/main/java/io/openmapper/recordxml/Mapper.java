package io.openmapper.recordxml;

import io.vavr.collection.HashMap;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import org.w3c.dom.Document;

import io.openmapper.recordxml.util.SoftenEx;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import java.lang.reflect.RecordComponent;

public record Mapper(
        Map<Class<?>, ToXmlString<?>> toXmlString,
        Map<Class<?>, OfXmlString<?>> ofXmlString
) {

    static final Mapper STOCK = new Mapper(HashMap.empty(), HashMap.empty())
            .toXmlString(String.class, s->s)
            .ofXmlString(String.class, s->s)
            .toXmlString(Integer.class, i->Integer.toString(i))
            .ofXmlString(Integer.class, Integer::parseInt)
            .ofXmlString(int.class, Integer::parseInt);

    public static Mapper stock() {
        return STOCK;
    }

    public <T> Mapper toXmlString(Class<T> type, ToXmlString<T> toXmlString) {
        return new Mapper(this.toXmlString.put(type, toXmlString), ofXmlString);
    }

    public <T> Mapper ofXmlString(Class<T> type, OfXmlString<T> ofXmlString) {
        return new Mapper(toXmlString, this.ofXmlString.put(type, ofXmlString));
    }

    public <T extends Record> Document recordToXml(DocumentBuilder documentBuilder, T record) {
        Document document =  documentBuilder.newDocument();
        Class<?> recordClass = record.getClass();
        Element root = document.createElement(recordClass.getSimpleName());
        document.appendChild(root);
        for (RecordComponent component: recordClass.getRecordComponents()) {
            Object value = SoftenEx.call(() -> component.getAccessor().invoke(record));

            @SuppressWarnings("rawtypes")
            ToXmlString toString =  toXmlString.get(value.getClass()).get();

            @SuppressWarnings("unchecked")
            String xmlString = toString.toXmlString(value);

            root.setAttribute(component.getName(), xmlString);
        }
        return document;
    }

    public <T> T xmlToRecord(Class<T> recordClass, Document document) {
        Element root = document.getDocumentElement();
        if (!root.getNodeName().equals(recordClass.getSimpleName())) {
            throw new IllegalArgumentException("The root element of the document is not " + recordClass.getSimpleName());
        }
        Class<?>[] componentTypes = Iterator.of(recordClass.getRecordComponents())
                .map(RecordComponent::getType)
                .toJavaArray(Class[]::new);

        Object[] componentValues = Iterator.of(recordClass.getRecordComponents())
                .map(c->{
                    String xmlString = root.getAttribute(c.getName());
                    return ofXmlString.get(c.getType()).get().ofXmlString(xmlString);
                })
                .toJavaArray();

        return SoftenEx.call(() -> recordClass
                .getConstructor(componentTypes)
                .newInstance(componentValues));
    }


}
