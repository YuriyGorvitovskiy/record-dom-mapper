package io.openmapper.recordxml;

import io.vavr.collection.*;
import io.vavr.control.Option;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public record RecordToDomMapper(
        Map<Class<?>, PrimitiveSerializer> primitiveSerializerByClass,
        ContainerSerializer containerSerializer
) {

    // Static factory method to create a new instance
    @SuppressWarnings("DataFlowIssue")
    public static RecordToDomMapper stock() {
        return new RecordToDomMapper(
                HashMap.<Class<?>, PrimitiveSerializer>empty()
                        .put(boolean.class, v-> Option.some(Boolean.toString((Boolean)v)))
                        .put(Boolean.class, v->Option.when(null != v, () -> Boolean.toString((Boolean)v)))
                        .put(byte.class, v-> Option.some(Byte.toString((Byte)v)))
                        .put(Byte.class, v->Option.when(null != v, () -> Byte.toString((Byte)v)))
                        .put(short.class, v-> Option.some(Short.toString((Short)v)))
                        .put(Short.class, v->Option.when(null != v, () -> Short.toString((Short)v)))
                        .put(int.class, v-> Option.some(Integer.toString((Integer)v)))
                        .put(Integer.class, v->Option.when(null != v, () -> Integer.toString((Integer)v)))
                        .put(long.class, v-> Option.some(Long.toString((Long)v)))
                        .put(Long.class, v->Option.when(null != v, () -> Long.toString((Long)v)))
                        .put(float.class, v-> Option.some(Float.toString((Float)v)))
                        .put(Float.class, v->Option.when(null != v, () -> Float.toString((Float)v)))
                        .put(double.class, v-> Option.some(Double.toString((Double)v)))
                        .put(Double.class, v->Option.when(null != v, () -> Double.toString((Double)v)))
                        .put(String.class, v-> Option.when(null != v, ()-> (String)v))
                        .put(CharSeq.class, v-> Option.when(null != v, ()-> ((CharSeq)v).mkString()))
                        .put(Date.class, v-> Option.when(null != v, ()-> DateTimeFormatter.ISO_INSTANT.format(((Date)v).toInstant())))
                        .put(Instant.class, v-> Option.when(null != v, ()-> DateTimeFormatter.ISO_INSTANT.format((Instant)v))),
                VavrSerializer.stock()
        );
    }

    public Document mapToDom(Object value) {
        try {
            // Create a new XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.newDocument();

            final String rootName = value == null ? "null" : value.getClass().getSimpleName();
            document.appendChild(
                    serializeObject(document, XmlBuilder.element(rootName), value)
                        .headOption()
                        .map(n->(Element)n)
                        .getOrElse(()->document.createElement(rootName)));

            return document;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map record to DOM", e);
        }
    }
    <N extends Node> Traversable<? extends N> serializeObject(Document doc, XmlBuilder<N> builder, Object value) {
        if (null == value) {
            return Stream.empty();
        }

        Class<?> valueClass = value.getClass();
        Option<PrimitiveSerializer> primitiveOpt = primitiveSerializerByClass.get(valueClass);
        if (primitiveOpt.isDefined()) {
            return primitiveOpt.get()
                    .serialize(value)
                    .flatMap(xmlValue -> builder.build(doc, xmlValue))
                    .toStream();
        }

        Option<Traversable<?>> entriesOpt = containerSerializer().getEntries(value);
        if (entriesOpt.isDefined()) {
            Traversable<Node> children = entriesOpt.get()
                    .flatMap(entry-> serializeObject(doc, XmlBuilder.element(defaultName(entry)), entry));

            return builder.build(doc, children);
        }
        RecordComponent[] components =  valueClass.getRecordComponents();
        if (null == components) {
            throw new UnsupportedOperationException("Type " + valueClass + " is not supported");
        }

        Traversable<Node> children = Array.of(components)
                .flatMap( component -> {
                    try {
                        return serializeObject(doc, XmlBuilder.element(component.getName()), component.getAccessor().invoke(value));
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to serialize record component " + component.getName(), e);
                    }
                });
        return builder.build(doc, children);
    }

    String defaultName(Object value) {
        return value == null ? "null" : value.getClass().getSimpleName();
    }

}