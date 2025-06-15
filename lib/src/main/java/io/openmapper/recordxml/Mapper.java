package io.openmapper.recordxml;

import io.vavr.collection.*;
import io.vavr.control.Option;
import org.w3c.dom.Document;

import io.openmapper.recordxml.util.SoftenEx;

import javax.xml.parsers.DocumentBuilder;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public record Mapper(
        Map<Class<?>, ToXmlString<?>> toXmlString,
        Map<Class<?>, OfXmlString<?>> ofXmlString
) {

    static final Mapper STOCK = new Mapper(HashMap.empty(), HashMap.empty())
            .toXmlString(Boolean.class, b->Boolean.toString(b))
            .toXmlString(Byte.class, b->Byte.toString(b))
            .toXmlString(Date.class, d-> DateTimeFormatter.ISO_INSTANT.format(d.toInstant()))
            .toXmlString(Double.class, d->Double.toString(d))
            .toXmlString(Float.class, f->Float.toString(f))
            .toXmlString(Instant.class, DateTimeFormatter.ISO_INSTANT::format)
            .toXmlString(Integer.class, i->Integer.toString(i))
            .toXmlString(Long.class, l->Long.toString(l))
            .toXmlString(Short.class, s->Short.toString(s))
            .toXmlString(String.class, s->s)
            .ofXmlString(boolean.class, Boolean::parseBoolean)
            .ofXmlString(Boolean.class, Boolean::parseBoolean)
            .ofXmlString(byte.class, Byte::parseByte)
            .ofXmlString(Byte.class, Byte::parseByte)
            .ofXmlString(Date.class, s-> Date.from(Instant.parse(s)))
            .ofXmlString(double.class, Double::parseDouble)
            .ofXmlString(Double.class, Double::parseDouble)
            .ofXmlString(float.class, Float::parseFloat)
            .ofXmlString(Float.class, Float::parseFloat)
            .ofXmlString(Instant.class, Instant::parse)
            .ofXmlString(int.class, Integer::parseInt)
            .ofXmlString(Integer.class, Integer::parseInt)
            .ofXmlString(long.class, Long::parseLong)
            .ofXmlString(Long.class, Long::parseLong)
            .ofXmlString(short.class, Short::parseShort)
            .ofXmlString(Short.class, Short::parseShort)
            .ofXmlString(String.class, s->s);

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
        org.w3c.dom.Element root = recordToXml(recordClass.getSimpleName(), record).toDOM(document);
        document.appendChild(root);
        return document;
    }

    public <T extends Record> io.openmapper.recordxml.Element recordToXml(String name, T record) {
        Class<?> recordClass = record.getClass();
        var attributesAndNodes = Iterator.of(recordClass.getRecordComponents())
                .flatMap(c-> valueToXml(c.getName(), false, SoftenEx.call(() -> c.getAccessor().invoke(record))))
                .partition(u->u instanceof Attribute);

        return io.openmapper.recordxml.Element.of(name)
                .withAttributes(attributesAndNodes._1.map(a->(Attribute)a))
                .withChildren(attributesAndNodes._2.map(a->(Node)a));
    }

    private Iterable<Unit> valueToXml(String name, boolean forceNode, Object value) {
        if (null == value) {
            return Option.none();
        }
        Option<ToXmlString<?>> toString =  toXmlString.get(value.getClass());
        if (toString.isDefined()) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            String xmlString = ((ToXmlString)toString.get()).toXmlString(value);
            return Option.of(forceNode
                            ? io.openmapper.recordxml.Element.of(name).withChildren(Text.of(xmlString))
                            : Attribute.of(name, xmlString));
        }
        return switch(value) {
            case Option<?> o -> o.map(v->valueToXml(name, false, v)).getOrElse(Option.none());
            case Enum<?> e -> Option.of(Attribute.of(name, e.name()));
            case Record r -> Option.of(recordToXml(name, r));
            case Iterable<?> i -> Iterator.ofAll(i).flatMap(v->valueToXml(name, true, v));
            default ->  throw new IllegalArgumentException("Unsupported type " + value.getClass());
        };
    }


    public <T> T xmlToRecord(Class<T> recordClass, Document document) {
        org.w3c.dom.Element root = document.getDocumentElement();
        if (!root.getNodeName().equals(recordClass.getSimpleName())) {
            throw new IllegalArgumentException("The root element of the document is not " + recordClass.getSimpleName());
        }
        Class<?>[] componentTypes = Iterator.of(recordClass.getRecordComponents())
                .map(RecordComponent::getType)
                .toJavaArray(Class[]::new);

        Object[] componentValues = Iterator.of(recordClass.getRecordComponents())
                .map(c->{
                    Option<OfXmlString<?>> ofString = ofXmlString.get(c.getType());
                    if (ofString.isDefined()) {
                        String xmlString = root.getAttribute(c.getName());
                        return ofString.get().ofXmlString(xmlString);
                    }
                    if (Enum.class.isAssignableFrom(c.getType())) {
                        String xmlString = root.getAttribute(c.getName());
                        @SuppressWarnings({"rawtypes", "unchecked"})
                        Enum value = Enum.valueOf((Class)c.getType(), xmlString);
                        return value;
                    }
                    if (Option.class.isAssignableFrom(c.getType())) {
                        if (!root.hasAttribute(c.getName())) {
                            return Option.none();
                        }
                        String xmlString = root.getAttribute(c.getName());
                        Type type =  ((ParameterizedType)c.getGenericType()).getActualTypeArguments()[0];
                        ofString = ofXmlString.get((Class<?>)type);
                        if (ofString.isDefined()) {
                            return Option.of(ofString.get().ofXmlString(xmlString));
                        }
                        if (Enum.class.isAssignableFrom(c.getType())) {
                            @SuppressWarnings({"rawtypes", "unchecked"})
                            Enum value = Enum.valueOf((Class)c.getType(), xmlString);
                            return Option.of(value);
                        }
                    }
                    throw new IllegalArgumentException("Unsupported type " + c.getGenericType());
                })
                .toJavaArray();

        return SoftenEx.call(() -> recordClass
                .getConstructor(componentTypes)
                .newInstance(componentValues));
    }


}
