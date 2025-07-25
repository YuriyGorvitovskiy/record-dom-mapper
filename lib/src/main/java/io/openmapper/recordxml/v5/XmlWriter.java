package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlText;

public record XmlWriter(Config config) {
    public XmlElement toXml(String rootName, Object value) {
        XmlElement root = XmlElement.of(rootName);
        if (value == null) {
            return root;
        }

        return switch (config.mapperFor(value.getClass())) {
            case ComplexMapper complex -> complex.toXml(value).map(p -> p.name(rootName)).getOrElse(root);
            case EmbeddedMapper embedded -> embedded.toXml(value).map(p -> p.name(rootName)).getOrElse(root);
            case SimpleMapper simple -> root.withChildren(XmlText.of(simple.toXml(value)));
            case ChoiceMapper ignored ->
                    throw new UnsupportedOperationException("Choice is not supported for root element");
            case SequenceMapper ignored ->
                    throw new UnsupportedOperationException("Sequence is not supported for root element");
        };
    }
}
