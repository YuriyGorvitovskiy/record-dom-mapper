package io.openmapper.recordxml.v3.context;

import java.lang.reflect.Type;
import java.util.Objects;

import io.openmapper.recordxml.util.Strings;
import io.openmapper.recordxml.v3.descriptor.SequenceDescriptor;
import io.openmapper.recordxml.v3.schema.Schema;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.collection.Array;
import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record SequenceContext<T>(Option<String> nameOpt,
                                 SequenceDescriptor<T> descriptor,
                                 Context<?> entryContext,
                                 Type declaredType) implements ContainerContext<T> {

    public static <T> SequenceContext<T> of(Schema schema, String name, SequenceDescriptor<T> descriptor, Type declaredType) {
        Context<?> entryContext = schema.context(true, Option.of(name), descriptor.entryType(declaredType));

        return new SequenceContext<>(
                Option.when(!Objects.equals(name, entryContext.name()), name),
                descriptor,
                entryContext,
                declaredType);
    }

    @Override
    public SequenceContext<T> withoutName() {
        return new SequenceContext<>(Option.none(), descriptor, entryContext, declaredType);
    }

    @Override
    public boolean isElement() {
        return true;
    }

    @Override
    public String name() {
        return nameOpt.getOrElse(entryContext.name());
    }

    @Override
    public Iterable<? extends XmlUnit> toXml(Object object) {
        @SuppressWarnings("unchecked")
        T value = (T) object;

        var units = Iterator.ofAll(descriptor.entries(value))
                .flatMap(entryContext::toXml);

        return nameOpt.isDefined()
                ? Option.of(XmlElement.of(nameOpt.get()).withUnits(units))
                : units;
    }

    @Override
    public T ofXml(XmlElement xml) {
        if (nameOpt.isDefined()) {
            Option<XmlElement> element = xml.elements().find(e -> Objects.equals(nameOpt.get(), e.name()));
            if (element.isEmpty()) {
                return descriptor.ofEntries(declaredType, Array.empty());
            }
            xml = element.get();
        }

        Seq<XmlElement> children = xml.elements();
        String entryName = entryContext.name();
        if (Strings.notEmpty(entryName)) {
            children = children.filter(e -> Objects.equals(entryName, e.name()));
        }

        Seq<?> value = children
                .map(entryContext::ofXml)
                .filter(Objects::nonNull);

        return descriptor.ofEntries(declaredType, value);
    }
}
