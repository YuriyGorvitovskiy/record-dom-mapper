package io.openmapper.recordxml.v5;

import java.util.function.Supplier;

import io.openmapper.recordxml.v5.xsd.complexType;
import io.openmapper.recordxml.v5.xsd.element;
import io.openmapper.recordxml.v5.xsd.schema;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;

public record XsdBuilder(Config config) {

    public schema build(String name, Class<?> clazz) {
        Tuple2<TypeName, Seq<complexType>> rootAndTypes = build(clazz);
        return schema.of(
                element.of(name, rootAndTypes._1),
                rootAndTypes._2);
    }

    Tuple2<TypeName, Seq<complexType>> build(Class<?> clazz) {
        XsdEntry<TypeName> rootType = switch (config.mapperFor(clazz)) {
            case ComplexMapper complex -> complex(complex);
            case EmbeddedMapper embedded -> embedded(embedded);
            case SimpleMapper simple -> simple(clazz, simple);
            case ChoiceMapper ignored ->
                    throw new UnsupportedOperationException("Choice is not supported for root element");
            case SequenceMapper ignored ->
                    throw new UnsupportedOperationException("Sequence is not supported for root element");
        };
        Seq<complexType> types = List.empty();
        Set<TypeName> processed = HashSet.empty();
        Map<TypeName, Supplier<XsdEntry<complexType>>> typesMap = rootType.types();
        do {
            Seq<XsdEntry<complexType>> iter = typesMap
                    .values()
                    .map(Supplier::get);

            processed = processed.addAll(iter.map(x -> TypeName.of(x.entry().name())));
            types = types.prependAll(iter.map(XsdEntry::entry));
            typesMap = iter
                    .foldLeft(
                            HashMap.<TypeName, Supplier<XsdEntry<complexType>>>empty(),
                            (m, x) -> m.merge(x.types()))
                    .removeAll(processed);
        } while (!typesMap.isEmpty());

        return Tuple.of(rootType.entry(), types);
    }

    XsdEntry<TypeName> complex(ComplexMapper complex) {
        return complex.xsd();
    }

    XsdEntry<TypeName> embedded(EmbeddedMapper embedded) {
        XsdEntry<complexType> type = embedded.xsd();
        return XsdEntry.complex(type);
    }

    XsdEntry<TypeName> simple(Class<?> clazz, SimpleMapper simple) {
        TypeName name = TypeName.of("RootOf_" + clazz.getSimpleName());

        XsdEntry<TypeName> xsd = simple.xsd();
        XsdEntry<complexType> type = XsdEntry
                .complexTypeWithSimpleContent(name, xsd.entry())
                .withTypes(xsd.types());

        return XsdEntry.complex(type);
    }
}
