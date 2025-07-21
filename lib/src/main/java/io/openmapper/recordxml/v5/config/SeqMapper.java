package io.openmapper.recordxml.v5.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.openmapper.recordxml.v5.*;
import io.openmapper.recordxml.v5.xsd.Occur;
import io.openmapper.recordxml.v5.xsd.choice;
import io.openmapper.recordxml.v5.xsd.complexType;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlPlainElement;
import io.openmapper.recordxml.xml.XmlText;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public interface SeqMapper {

    static Mapper of(Config config, Type declaredType) {
        if (!(declaredType instanceof ParameterizedType parametrizedType)) {
            throw new IllegalArgumentException("The declared type must be a parameterized type");
        }
        Mapper valueMapper = config.mapperFor(parametrizedType.getActualTypeArguments()[0]);

        return switch (valueMapper) {
            case ChoiceMapper choiceMapper -> new ChoiceSeqMapper(choiceMapper);
            case ComplexMapper complexMapper -> new ComplexSeqMapper(complexMapper);
            case SimpleMapper simpleMapper -> new SimpleSeqMapper(simpleMapper);
            case EmbeddedMapper ignored -> throw new RuntimeException("Embedded Mapper does not supported for seq");
            case SequenceMapper ignored -> throw new RuntimeException("Sequence Mapper does not supported for seq");
        };
    }
}

record SimpleSeqMapper(SimpleMapper entry) implements SequenceMapper {

    @Override
    public Seq<XmlPlainElement> toXml(Object value) {
        Seq<?> seq = (value == null ? Array.empty() : (Seq<?>) value);
        return seq.map(e -> XmlPlainElement.empty()
                .addChildren(XmlText.of(entry.toXml(e))));
    }

    @Override
    public Object ofXml(Seq<XmlElement> xml) {
        return xml.map(e -> entry.ofXml(e.text()));
    }

    @Override
    public XsdEntry<complexType> xsd() {
        XsdEntry<TypeName> entryXsd = entry.xsd();
        TypeName name = TypeName.of("EntryOf_" + entryXsd.entry().simpleName());

        return XsdEntry.complexTypeWithSimpleContent(name, entryXsd.entry());
    }
}

record ComplexSeqMapper(ComplexMapper entry) implements SequenceMapper {

    @Override
    public Seq<XmlPlainElement> toXml(Object value) {
        Seq<?> seq = (value == null ? Array.empty() : (Seq<?>) value);
        return seq.flatMap(entry::toXml);
    }

    @Override
    public Object ofXml(Seq<XmlElement> xml) {
        return xml.map(entry::ofXml);
    }

    @Override
    public XsdEntry<complexType> xsd() {
        XsdEntry<TypeName> entryXsd = entry.xsd();
        TypeName name = TypeName.of("EntryOf_" + entryXsd.entry().simpleName());

        return XsdEntry.complexTypeWithComplexContent(name, entryXsd);
    }
}

record ChoiceSeqMapper(ChoiceMapper entry) implements EmbeddedMapper {

    @Override
    public Set<String> names() {
        return entry.names();
    }

    @Override
    public Option<XmlPlainElement> toXml(Object value) {
        Seq<?> seq = (value == null ? Array.empty() : (Seq<?>) value);
        Seq<XmlElement> children = seq.flatMap(entry::toXml);

        return Option.when(!children.isEmpty(),
                () -> XmlPlainElement.empty().addChildren(children));
    }

    @Override
    public Object ofXml(XmlElement xml) {
        return xml.elements().map(entry::ofXml);
    }

    @Override
    public XsdEntry<complexType> xsd() {
        XsdEntry<choice> entryXsd = entry.xsd();
        TypeName name = TypeName.of("SeqOf_" + entry.name());

        choice group = entryXsd.entry().withOccurs(Occur.ZERO, Occur.UNBOUND);

        return XsdEntry.complexType(name, group).withTypes(entryXsd.types());
    }

}
