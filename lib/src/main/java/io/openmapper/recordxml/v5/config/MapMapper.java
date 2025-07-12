package io.openmapper.recordxml.v5.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.openmapper.recordxml.v5.*;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.openmapper.recordxml.xml.XmlText;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public interface MapMapper {

    static Mapper of(Config config, Type declaredType) {
        if (!(declaredType instanceof ParameterizedType parametrizedType)) {
            throw new IllegalArgumentException("The declared type must be a parameterized type");
        }
        Mapper keyMapper = config.mapperFor(parametrizedType.getActualTypeArguments()[0]);
        Mapper valueMapper = config.mapperFor(parametrizedType.getActualTypeArguments()[1]);

        if (!(keyMapper instanceof SimpleMapper keySimpleMapper)) {
            throw new IllegalArgumentException("The key mapper must be a simple type");
        }

        return switch (valueMapper) {
            case ChoiceMapper choiceMapper -> new ChoiceMapMapper(keySimpleMapper, choiceMapper);
            case ComplexMapper complexMapper -> new ComplexMapMapper(keySimpleMapper, complexMapper);
            case SimpleMapper simpleMapper -> new SimpleMapMapper(keySimpleMapper, simpleMapper);
            case EmbeddedMapper ignored -> throw new RuntimeException("Embedded Mapper does not supported for map");
            case SequenceMapper ignored -> throw new RuntimeException("Sequence Mapper does not supported for map");
        };
    }
}

record SimpleMapMapper(SimpleMapper key, SimpleMapper entry) implements SequenceMapper {

    @Override
    public Seq<XmlPlainElement> toXml(Object value) {
        Map<?, ?> map = (value == null ? HashMap.empty() : (Map<?, ?>) value);
        return map.map(t -> XmlPlainElement.empty()
                .addAttribute("Key", key.toXml(t._1))
                .addChildren(XmlText.of(entry.toXml(t._2))));
    }

    @Override
    public Object ofXml(Seq<XmlElement> xml) {
        return xml.toMap(
                e -> key.ofXml(e.attributes().get("Key").get()),
                e -> entry.ofXml(e.text()));
    }
}

record ComplexMapMapper(SimpleMapper key, ComplexMapper entry) implements SequenceMapper {

    @Override
    public Seq<XmlPlainElement> toXml(Object value) {
        Map<?, ?> map = (value == null ? HashMap.empty() : (Map<?, ?>) value);
        return map.flatMap(t -> entry
                .toXml(t._2)
                .map(e -> e.addAttribute("Key", key.toXml(t._1))));
    }

    @Override
    public Object ofXml(Seq<XmlElement> xml) {
        return xml.toMap(
                e -> key.ofXml(e.attributes().get("Key").get()),
                entry::ofXml);
    }
}

record ChoiceMapMapper(SimpleMapper key, ChoiceMapper entry) implements EmbeddedMapper {

    @Override
    public Set<String> names() {
        return entry.names();
    }

    @Override
    public Option<XmlPlainElement> toXml(Object value) {
        Map<?, ?> map = (value == null ? HashMap.empty() : (Map<?, ?>) value);
        Seq<XmlElement> children = map.flatMap(t -> entry
                .toXml(t._2)
                .map(e -> e.addAttribute("Key", key.toXml(t._1))));

        return Option.when(!children.isEmpty(),
                () -> XmlPlainElement.empty().addChildren(children));
    }

    @Override
    public Object ofXml(XmlElement xml) {
        return xml.elements().toMap(
                e -> key.ofXml(e.attributes().get("Key").get()),
                entry::ofXml);
    }
}
