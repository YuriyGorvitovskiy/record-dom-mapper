package io.openmapper.recordxml.v5.config;

import io.openmapper.recordxml.v5.ChoiceMapper;
import io.openmapper.recordxml.v5.EmbeddedMapper;
import io.openmapper.recordxml.v5.SimpleMapper;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record MapChoiceMapper(SimpleMapper key, ChoiceMapper entry) implements EmbeddedMapper {

    @Override
    public Option<XmlPlainElement> toXml(Object value) {
        Map<?, ?> map = (value == null ? HashMap.empty() : (Map<?, ?>) value);
        Seq<XmlElement> children = map.flatMap(t -> entry
                .toXml(t._2)
                .map(e -> e.addAttribute("Key", key.toXml(t._1))));

        return Option.when(!children.isEmpty(),
                () -> XmlPlainElement.empty().addChildren(children));
    }
}
