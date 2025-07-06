package io.openmapper.recordxml.v5.config;

import io.openmapper.recordxml.v5.SequenceMapper;
import io.openmapper.recordxml.v5.SimpleMapper;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.openmapper.recordxml.xml.XmlText;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public record MapSimpleMapper(SimpleMapper key, SimpleMapper entry) implements SequenceMapper {

    @Override
    public Seq<XmlPlainElement> toXml(Object value) {
        Map<?, ?> map = (value == null ? HashMap.empty() : (Map<?, ?>) value);
        return map.map(t -> XmlPlainElement.empty()
                .addAttribute("Key", key.toXml(t._1))
                .addChildren(XmlText.of(entry.toXml(t._2))));
    }
}
