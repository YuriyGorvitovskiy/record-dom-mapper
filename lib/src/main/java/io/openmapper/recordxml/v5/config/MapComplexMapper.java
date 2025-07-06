package io.openmapper.recordxml.v5.config;

import io.openmapper.recordxml.v5.ComplexMapper;
import io.openmapper.recordxml.v5.SequenceMapper;
import io.openmapper.recordxml.v5.SimpleMapper;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public record MapComplexMapper(SimpleMapper key, ComplexMapper entry) implements SequenceMapper {

    @Override
    public Seq<XmlPlainElement> toXml(Object value) {
        Map<?, ?> map = (value == null ? HashMap.empty() : (Map<?, ?>) value);
        return map.flatMap(t -> entry
                .toXml(t._2)
                .map(e -> e.addAttribute("Key", key.toXml(t._1))));
    }
}
