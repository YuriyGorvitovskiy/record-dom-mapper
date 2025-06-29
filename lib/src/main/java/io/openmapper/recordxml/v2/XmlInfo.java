package io.openmapper.recordxml.v2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;

import io.vavr.collection.*;
import io.vavr.control.Option;

public record XmlInfo(
        SourceType sourceType,
        XmlType xmlType,
        String xmlName,
        Map<String, XmlInfo> attributes,
        Map<String, XmlInfo> children) {

    public enum SourceType {
        NULLABLE,
        STRINGIFIABLE,
        OPTION,
        SEQUENCE,
        MAP,
        INTERFACE,
        RECORD,
    }

    public enum XmlType {
        NONE,
        ATTRIBUTE,
        ELEMENT,
        TEXT,
    }

    public static XmlInfo of(Mapper mapper, Class<? extends Record> recordClass) {
        return ofRecord(mapper, recordClass)
                .withElementName(recordClass.getSimpleName());
    }

    public static XmlInfo of(Mapper mapper, RecordComponent component) {
        Type type = component.getGenericType();
        return switch (type) {
            case Class<?> clazz -> ofNullable(mapper, component, clazz);
            case ParameterizedType clazz when Option.class.isAssignableFrom((Class<?>) clazz.getRawType()) ->
                    ofOption(mapper, component, clazz.getActualTypeArguments()[0]);
            case ParameterizedType clazz when Map.class.isAssignableFrom((Class<?>) clazz.getRawType()) ->
                    ofMap(mapper, component, clazz.getActualTypeArguments()[0], clazz.getActualTypeArguments()[1]);
            case ParameterizedType clazz when Traversable.class.isAssignableFrom((Class<?>) clazz.getRawType()) ->
                    ofTraversable(mapper, component, clazz.getActualTypeArguments()[0]);
            case null, default -> throw new IllegalStateException("Unexpected component type: " + type);
        };
    }

    public static XmlInfo of(Mapper mapper, Type type) {
        return switch (type) {
            case Class<?> clazz when mapper.toXmlString().containsKey(clazz) -> ofStringifiable();
            case Class<?> clazz when Enum.class.isAssignableFrom(clazz) -> ofStringifiable();
            case Class<?> clazz when Record.class.isAssignableFrom(clazz) -> ofRecord(mapper, clazz);
            case Class<?> clazz when clazz.isInterface() && clazz.isSealed() -> ofInterface(mapper, clazz);
            case null, default -> throw new IllegalStateException("Unexpected component type: " + type);
        };
    }

    static XmlInfo ofStringifiable() {
        return new XmlInfo(
                SourceType.STRINGIFIABLE,
                XmlType.TEXT,
                "",
                HashMap.empty(),
                HashMap.empty());
    }

    static XmlInfo ofRecord(Mapper mapper, Class<?> clazz) {
        var attributesAndElements = Array.of(clazz.getRecordComponents())
                .map(c -> of(mapper, c))
                .partition(u -> u.xmlType == XmlType.ATTRIBUTE);

        return new XmlInfo(
                SourceType.RECORD,
                XmlType.NONE,
                "",
                attributesAndElements._1.toMap(XmlInfo::xmlName, c -> c),
                attributesAndElements._2.toMap(XmlInfo::xmlName, c -> c));
    }

    static XmlInfo ofInterface(Mapper mapper, Class<?> clazz) {
        Seq<XmlInfo> children = collectPermittedRecords(clazz)
                .map(c -> ofRecord(mapper, c).withElementName(c.getSimpleName()));

        return new XmlInfo(
                SourceType.INTERFACE,
                XmlType.NONE,
                "",
                HashMap.empty(),
                children.toMap(XmlInfo::xmlName, c -> c));
    }


    static Seq<Class<?>> collectPermittedRecords(Class<?> sealedInterface) {
        var recordsAndInterfaces = Array.of(sealedInterface.getPermittedSubclasses())
                .partition(Record.class::isAssignableFrom);

        return recordsAndInterfaces._1.appendAll(recordsAndInterfaces._2
                .flatMap(XmlInfo::collectPermittedRecords));
    }

    static XmlInfo ofNullable(Mapper mapper, RecordComponent component, Type type) {
        XmlInfo child = of(mapper, type);
        return switch (child.sourceType) {
            case STRINGIFIABLE -> new XmlInfo(
                    SourceType.NULLABLE,
                    XmlType.ATTRIBUTE,
                    component.getName(),
                    HashMap.empty(),
                    HashMap.of("", child));
            case RECORD, INTERFACE -> new XmlInfo(
                    SourceType.NULLABLE,
                    XmlType.ELEMENT,
                    component.getName(),
                    HashMap.empty(),
                    HashMap.of("", child));
            default -> throw new IllegalStateException("Unexpected value: " + child.sourceType);
        };
    }

    static XmlInfo ofOption(Mapper mapper, RecordComponent component, Type type) {
        XmlInfo child = of(mapper, type);
        return switch (child.sourceType) {
            case STRINGIFIABLE -> new XmlInfo(
                    SourceType.OPTION,
                    XmlType.ATTRIBUTE,
                    component.getName(),
                    HashMap.empty(),
                    HashMap.of("", child));
            case RECORD, INTERFACE -> new XmlInfo(
                    SourceType.OPTION,
                    XmlType.ELEMENT,
                    component.getName(),
                    HashMap.empty(),
                    HashMap.of("", child));
            default -> throw new IllegalStateException("Unexpected value: " + child.sourceType);
        };
    }

    static XmlInfo ofTraversable(Mapper mapper, RecordComponent component, Type clazz) {
        XmlInfo child = of(mapper, clazz);
        return switch (child.sourceType) {
            case STRINGIFIABLE, RECORD, INTERFACE -> new XmlInfo(
                    SourceType.SEQUENCE,
                    XmlType.ELEMENT,
                    component.getName(),
                    HashMap.empty(),
                    HashMap.of("", child));
            default -> throw new IllegalStateException("Unexpected value: " + child.sourceType);
        };
    }


    static XmlInfo ofMap(Mapper mapper, RecordComponent component, Type key, Type clazz) {
        XmlInfo keyInfo = of(mapper, key);
        if (keyInfo.sourceType != SourceType.STRINGIFIABLE) {
            throw new IllegalArgumentException("The key of a map must be a stringifiable type");
        }
        XmlInfo child = of(mapper, clazz);

        return switch (child.sourceType) {
            case STRINGIFIABLE, RECORD, INTERFACE -> new XmlInfo(
                    SourceType.MAP,
                    XmlType.ELEMENT,
                    component.getName(),
                    HashMap.of(Mapper.KEY, keyInfo),
                    HashMap.of("", child));
            default -> throw new IllegalStateException("Unexpected value: " + child.sourceType);
        };
    }

    public XmlInfo withElementName(String xmlName) {
        return new XmlInfo(
                sourceType,
                XmlType.ELEMENT,
                xmlName,
                attributes,
                children);
    }
}
