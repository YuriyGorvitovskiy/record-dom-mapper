package io.openmapper.recordxml.v5.config;

import java.lang.reflect.Type;

import io.openmapper.recordxml.util.Java;
import io.openmapper.recordxml.v5.*;
import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record InterfaceMapper(Map<Class<?>, ImplementationInfo> infoByType) implements ChoiceMapper {

    record ImplementationInfo(String name, Mapper mapper) {
    }

    public static InterfaceMapper of(Config config, Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);

        Seq<Class<?>> permitted = Java.collectPermittedRecords(rawClass);
        Map<Class<?>, ImplementationInfo> infoByType = permitted
                .toMap(c -> c,
                        c -> new ImplementationInfo(
                                c.getSimpleName(),
                                config.mapperFor(c)));

        return new InterfaceMapper(infoByType);
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
}
