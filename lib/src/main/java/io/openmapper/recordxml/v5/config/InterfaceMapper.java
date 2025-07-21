package io.openmapper.recordxml.v5.config;

import java.lang.reflect.Type;

import io.openmapper.recordxml.util.Java;
import io.openmapper.recordxml.v5.*;
import io.openmapper.recordxml.v5.xsd.Occur;
import io.openmapper.recordxml.v5.xsd.choice;
import io.openmapper.recordxml.v5.xsd.group;
import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public record InterfaceMapper(String name,
                              Map<Class<?>, ImplementationInfo> infoByType,
                              Map<String, ImplementationInfo> infoByName) implements ChoiceMapper {

    record ImplementationInfo(Class<?> clazz, String name, Mapper mapper) {
    }

    public static InterfaceMapper of(Config config, Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);

        Seq<Class<?>> permitted = Java.collectPermittedRecords(rawClass);
        Seq<ImplementationInfo> infos = permitted.map(
                c -> new ImplementationInfo(
                        c,
                        extractName(c),
                        config.mapperFor(c)));

        return new InterfaceMapper(
                rawClass.getSimpleName(),
                infos.toMap(ImplementationInfo::clazz, i -> i),
                infos.toMap(ImplementationInfo::name, i -> i));
    }

    static String extractName(Class<?> clazz) {
        XmlNameSpace annotation = clazz.getAnnotation(XmlNameSpace.class);
        if (annotation != null) {
            return annotation.value() + ":" + clazz.getSimpleName();
        }
        return clazz.getSimpleName();
    }

    @Override
    public Set<String> names() {
        return infoByName.keySet();
    }

    @Override
    public Option<XmlElement> toXml(Object value) {
        if (value == null) {
            return Option.none();
        }
        Class<?> clazz = value.getClass();
        ImplementationInfo info = infoByType.get(clazz).get();
        return switch (info.mapper) {
            case SimpleMapper s -> Option.of(XmlElement.ofText(info.name, s.toXml(value)));
            case ComplexMapper c -> c.toXml(value).map(p -> p.name(info.name));
            case EmbeddedMapper c -> c.toXml(value).map(p -> p.name(info.name));
            case SequenceMapper ignored ->
                    throw new RuntimeException("Sequence Mapper does not supported for interface");
            case ChoiceMapper ignored -> throw new RuntimeException("Choice Mapper does not supported for interface");
        };
    }

    @Override
    public Object ofXml(XmlElement xml) {
        ImplementationInfo info = infoByName.get(xml.name())
                .getOrElseThrow(() -> new RuntimeException("No implementation found for " + xml.name()));

        return switch (info.mapper) {
            case SimpleMapper s -> s.ofXml(xml.text());
            case ComplexMapper c -> c.ofXml(xml);
            case EmbeddedMapper c -> c.ofXml(xml);
            case ChoiceMapper ignored -> throw new RuntimeException("Choice Mapper does not supported for interface");
            case SequenceMapper ignored ->
                    throw new RuntimeException("Sequence Mapper does not supported for interface");
        };
    }

    @Override
    public XsdEntry<choice> xsd() {
        Seq<XsdEntry<group>> choices = Seq.narrow(infoByName.values().map(this::xsd));
        return XsdEntry.choice(choices, Occur.ZERO, Occur.DEFAULT);
    }

    XsdEntry<group> xsd(ImplementationInfo info) {
        return switch (info.mapper) {
            case SimpleMapper s -> XsdEntry.elementSimple(info.name, s.xsd());
            case ComplexMapper c -> XsdEntry.elementOfType(info.name, c.xsd());
            case EmbeddedMapper c -> XsdEntry.elementOfComplexType(info.name, c.xsd());
            case ChoiceMapper ignored -> throw new RuntimeException("Choice Mapper does not supported for interface");
            case SequenceMapper ignored ->
                    throw new RuntimeException("Sequence Mapper does not supported for interface");
        };
    }


}
