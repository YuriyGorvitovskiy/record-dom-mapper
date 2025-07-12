package io.openmapper.recordxml.v5.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Function;

import io.openmapper.recordxml.util.Java;
import io.openmapper.recordxml.util.SoftenEx;
import io.openmapper.recordxml.v5.*;
import io.openmapper.recordxml.xml.XmlAttribute;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public record RecordMapper(Config config, Constructor<?> constructor,
                           Seq<ComponentInfo> components) implements ComplexMapper {
    record ComponentInfo(String name, Type declaredType, Function<Object, Object> accessor) {
        ComponentMapper withMappier(Config config) {
            return new ComponentMapper(name, declaredType, accessor, config.mapperFor(declaredType));
        }
    }

    record ComponentMapper(String name, Type declaredType, Function<Object, Object> accessor, Mapper mapper) {
    }

    public static RecordMapper of(Config config, Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        Array<RecordComponent> components = Array.of(rawClass.getRecordComponents());
        Seq<ComponentInfo> infos = components.map(c -> new ComponentInfo(
                c.getName(),
                c.getGenericType(),
                r -> SoftenEx.call(() -> c.getAccessor().invoke(r))));

        Constructor<?> constructor = SoftenEx.call(() -> rawClass.getDeclaredConstructor(infos
                .map(c -> Java.rawClass(c.declaredType()))
                .toJavaArray(Class[]::new)));

        return new RecordMapper(config, constructor, infos);
    }

    @Override
    public Option<XmlPlainElement> toXml(Object value) {
        if (value == null) {
            return Option.none();
        }
        Seq<ComponentMapper> mappers = components.map(c -> c.withMappier(config));
        boolean requiredFieldElement = requiredFieldElement(mappers);
        Seq<XmlUnit> units = mappers
                .flatMap(m -> switch (m.mapper) {
                    case ChoiceMapper choice when requiredFieldElement ->
                            toChoiceField(choice, m.name, m.accessor.apply(value));
                    case ChoiceMapper choice -> toChoice(choice, m.accessor.apply(value));
                    case EmbeddedMapper embedded when requiredFieldElement ->
                            toEmbeddedField(embedded, m.name, m.accessor.apply(value));
                    case EmbeddedMapper embedded -> toEmbedded(embedded, m.accessor.apply(value));
                    case SequenceMapper sequence -> toSequence(sequence, m.name, m.accessor.apply(value));
                    case ComplexMapper complex -> toComplex(complex, m.name, m.accessor.apply(value));
                    case SimpleMapper simple -> toSimple(simple, m.name, m.accessor.apply(value));
                });
        return Option.of(XmlPlainElement.ofUnits(units));
    }

    @Override
    public Object ofXml(XmlElement xml) {
        Seq<ComponentMapper> mappers = components.map(c -> c.withMappier(config));
        boolean requiredFieldElement = requiredFieldElement(mappers);

        Seq<Object> values = mappers
                .map(m -> switch (m.mapper) {
                    case ChoiceMapper choice when requiredFieldElement -> ofChoiceField(choice, m.name, xml);
                    case ChoiceMapper choice -> ofChoice(choice, xml);
                    case EmbeddedMapper embedded when requiredFieldElement -> ofEmbeddedField(embedded, m.name, xml);
                    case EmbeddedMapper embedded -> ofEmbedded(embedded, xml);
                    case SequenceMapper sequence -> ofSequence(sequence, m.name, xml);
                    case ComplexMapper complex -> ofComplex(complex, m.name, xml);
                    case SimpleMapper simple -> ofSimple(simple, m.name, xml);
                });
        return SoftenEx.call(() -> constructor.newInstance(values.toJavaArray()));
    }

    Object ofChoiceField(ChoiceMapper mapper, String name, XmlElement xml) {
        Option<XmlElement> field = xml.elements().find(e -> Objects.equals(e.name(), name));
        if (field.isEmpty()) {
            return null;
        }
        return field.get()
                .elements()
                .headOption()
                .map(mapper::ofXml)
                .getOrNull();
    }

    Object ofChoice(ChoiceMapper mapper, XmlElement xml) {
        Set<String> names = mapper.names();
        return xml.elements()
                .find(e -> names.contains(e.name()))
                .map(mapper::ofXml)
                .getOrNull();
    }

    Object ofEmbeddedField(EmbeddedMapper mapper, String name, XmlElement xml) {
        return xml.elements()
                .find(e -> Objects.equals(e.name(), name))
                .map(mapper::ofXml)
                .getOrNull();
    }

    Object ofEmbedded(EmbeddedMapper mapper, XmlElement xml) {
        Set<String> names = mapper.names();
        Seq<XmlElement> children = xml.elements().filter(e -> names.contains(e.name()));
        return mapper.ofXml(xml.withChildren(children));
    }

    Object ofSequence(SequenceMapper mapper, String name, XmlElement xml) {
        return mapper.ofXml(xml
                .elements()
                .filter(e -> Objects.equals(e.name(), name)));
    }

    Object ofComplex(ComplexMapper mapper, String name, XmlElement xml) {
        return xml.elements()
                .find(e -> Objects.equals(e.name(), name))
                .map(mapper::ofXml)
                .getOrNull();
    }

    Object ofSimple(SimpleMapper mapper, String name, XmlElement xml) {
        return xml.attributes().get(name).map(mapper::ofXml).getOrNull();
    }

    boolean requiredFieldElement(Seq<ComponentMapper> mappers) {
        Seq<String> names = mappers
                .flatMap(m -> switch (m.mapper) {
                    case SimpleMapper ignore -> Option.none();
                    case ChoiceMapper choice -> choice.names();
                    case EmbeddedMapper embedded -> embedded.names();
                    default -> Option.of(m.name);
                });
        return names.distinct().size() != names.size();
    }

    Option<XmlElement> toChoiceField(ChoiceMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> XmlElement.of(name).withChildren(e));
    }

    Option<XmlElement> toChoice(ChoiceMapper mapper, Object value) {
        return mapper.toXml(value);
    }

    Option<XmlElement> toEmbeddedField(EmbeddedMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> e.name(name));
    }

    Seq<XmlUnit> toEmbedded(EmbeddedMapper mapper, Object value) {
        Option<XmlPlainElement> opt = mapper.toXml(value);
        if (opt.isEmpty()) {
            return Array.empty();
        }
        return opt.get().attributes()
                .<XmlUnit>map(t -> XmlAttribute.of(t._1, t._2))
                .appendAll(opt.get().children());
    }

    Seq<XmlElement> toSequence(SequenceMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> e.name(name));
    }

    Option<XmlElement> toComplex(ComplexMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> e.name(name));
    }

    Option<XmlUnit> toSimple(SimpleMapper mapper, String name, Object value) {
        if (value == null) {
            return Option.none();
        }
        String xml = mapper.toXml(value);
        return Option.when(xml != null, () -> XmlAttribute.of(name, xml));
    }

}
