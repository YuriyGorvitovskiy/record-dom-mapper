package io.openmapper.recordxml.v5.config;

import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
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
import io.vavr.control.Option;

public record RecordMapper(Config config, Seq<ComponentInfo> components) implements ComplexMapper {
    record ComponentInfo(String name, Type declaredType, Function<Object, Object> accessor) {
    }

    public static RecordMapper of(Config config, Type declaredType) {
        Array<RecordComponent> components = Array.of(Java.rawClass(declaredType).getRecordComponents());
        Seq<ComponentInfo> infos = components.map(c -> new ComponentInfo(
                c.getName(),
                c.getGenericType(),
                r -> SoftenEx.call(() -> c.getAccessor().invoke(r))));
        return new RecordMapper(config, infos);
    }

    @Override
    public Option<XmlPlainElement> toXml(Object value) {
        if (value == null) {
            return Option.none();
        }
        Seq<Mapper> mappers = components.map(c -> config.mapperFor(c.declaredType));
        boolean requiredFieldElement = mappers.count(m -> !(m instanceof SimpleMapper)) > 1;
        Seq<XmlUnit> units = components.zip(mappers)
                .flatMap(t -> switch (t._2) {
                    case ChoiceMapper choice when requiredFieldElement ->
                            choiceField(choice, t._1.name, t._1.accessor.apply(value));
                    case ChoiceMapper choice -> choice(choice, t._1.accessor.apply(value));
                    case EmbeddedMapper embedded when requiredFieldElement ->
                            embeddedField(embedded, t._1.name, t._1.accessor.apply(value));
                    case EmbeddedMapper embedded -> embedded(embedded, t._1.accessor.apply(value));
                    case SequenceMapper sequence -> sequence(sequence, t._1.name, t._1.accessor.apply(value));
                    case ComplexMapper complex -> complex(complex, t._1.name, t._1.accessor.apply(value));
                    case SimpleMapper simple -> simpe(simple, t._1.name, t._1.accessor.apply(value));
                });
        return Option.of(XmlPlainElement.ofUnits(units));
    }

    Option<XmlElement> choiceField(ChoiceMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> XmlElement.of(name).withChildren(e));
    }

    Option<XmlElement> choice(ChoiceMapper mapper, Object value) {
        return mapper.toXml(value);
    }

    Option<XmlElement> embeddedField(EmbeddedMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> e.name(name));
    }

    Seq<XmlUnit> embedded(EmbeddedMapper mapper, Object value) {
        Option<XmlPlainElement> opt = mapper.toXml(value);
        if (opt.isEmpty()) {
            return Array.empty();
        }
        return opt.get().attributes()
                .<XmlUnit>map(t -> XmlAttribute.of(t._1, t._2))
                .appendAll(opt.get().children());
    }

    Seq<XmlElement> sequence(SequenceMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> e.name(name));
    }

    Option<XmlElement> complex(ComplexMapper mapper, String name, Object value) {
        return mapper.toXml(value).map(e -> e.name(name));
    }

    Option<XmlUnit> simpe(SimpleMapper mapper, String name, Object value) {
        if (value == null) {
            return Option.none();
        }
        String xml = mapper.toXml(value);
        return Option.when(xml != null, () -> XmlAttribute.of(name, xml));
    }
}
