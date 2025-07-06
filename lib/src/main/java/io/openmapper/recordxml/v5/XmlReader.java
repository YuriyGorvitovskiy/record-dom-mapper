package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.xml.XmlElement;

public record XmlReader(Config config) {

    @SuppressWarnings("unchecked")
    public <T> T ofXml(Class<T> clazz, XmlElement root) {
        return (T) switch (config.mapperFor(clazz)) {
            case ChoiceMapper choice -> root.elements().headOption().map(choice::ofXml).getOrNull();
            case ComplexMapper complex -> complex.ofXml(root);
            case EmbeddedMapper embedded -> embedded.ofXml(root);
            case SimpleMapper simple -> simple.ofXml(root.text());
            case SequenceMapper ignored ->
                    throw new UnsupportedOperationException("Sequence is not supported for root element");
        };
    }
}
