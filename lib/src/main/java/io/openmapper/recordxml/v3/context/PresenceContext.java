package io.openmapper.recordxml.v3.context;

import java.lang.reflect.Type;
import java.util.Objects;

import io.openmapper.recordxml.util.Strings;
import io.openmapper.recordxml.v3.descriptor.PresenceDescriptor;
import io.openmapper.recordxml.v3.schema.Schema;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record PresenceContext<T>(Option<String> nameOpt,
                                 PresenceDescriptor<T> descriptor,
                                 Context<?> entryContext,
                                 Type declaredType) implements ContainerContext<T> {

    public static <T> PresenceContext<T> of(Schema schema, boolean forceElement, String name, PresenceDescriptor<T> descriptor, Type declaredType) {
        Context<?> entryContext = schema.context(forceElement, Option.of(name), descriptor.entryType(declaredType));

        return new PresenceContext<>(
                Option.when(!Objects.equals(name, entryContext.name()), name),
                descriptor,
                entryContext,
                declaredType);
    }

    @Override
    public PresenceContext<T> withoutName() {
        return new PresenceContext<>(Option.none(), descriptor, entryContext, declaredType);
    }

    @Override
    public boolean isElement() {
        return entryContext instanceof PrimitiveContext<?> p && p.element();
    }

    @Override
    public String name() {
        return nameOpt.getOrElse(entryContext.name());
    }

    @Override
    public Iterable<? extends XmlUnit> toXml(Object object) {
        @SuppressWarnings("unchecked")
        T value = (T) object;

        if (descriptor.isEmpty(value)) {
            return Option.none();
        }

        var units = entryContext.toXml(descriptor.entry(value));
        return nameOpt.isDefined()
                ? Option.of(XmlElement.of(nameOpt.get()).withUnits(units))
                : units;
    }

    @Override
    public T ofXml(XmlElement xml) {
        if (nameOpt.isDefined()) {
            Option<XmlElement> element = xml.elements().find(e -> Objects.equals(nameOpt.get(), e.name()));
            if (element.isEmpty()) {
                return descriptor.empty();
            }
            xml = element.get();
        }

        if (entryContext instanceof PrimitiveContext<?> p && p.element()) {
            Object value = entryContext.ofXml(xml);
            return toEntry(value);
        }

        Seq<XmlElement> children = xml.elements();
        String entryName = entryContext.name();
        if (Strings.notEmpty(entryName)) {
            children = children.filter(e -> Objects.equals(entryName, e.name()));
        }

        Object value = children.headOption()
                .map(entryContext::ofXml)
                .getOrNull();

        return toEntry(value);
    }

    T toEntry(Object value) {
        return value == null
                ? descriptor.empty()
                : descriptor.ofEntry(declaredType, value);
    }
}
