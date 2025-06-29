package io.openmapper.recordxml.v3.context;

import java.lang.reflect.Type;
import java.util.Objects;

import io.openmapper.recordxml.util.Strings;
import io.openmapper.recordxml.v3.descriptor.DictionaryDescriptor;
import io.openmapper.recordxml.v3.schema.Schema;
import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record DictionaryContext<T>(Option<String> nameOpt,
                                   DictionaryDescriptor<T> descriptor,
                                   Context<?> keyContext,
                                   Context<?> entryContext,
                                   Type declaredType) implements ContainerContext<T> {

    public static final Option<String> KEY = Option.of(":key");

    public static <T> DictionaryContext<T> of(Schema schema, String name, DictionaryDescriptor<T> descriptor, Type declaredType) {
        Context<?> keyContext = schema.context(false, KEY, descriptor.keyType(declaredType));
        Context<?> entryContext = schema.context(true, Option.of(name), descriptor.entryType(declaredType));

        return new DictionaryContext<>(
                Option.when(!Objects.equals(name, entryContext.name()), name),
                descriptor,
                keyContext,
                entryContext,
                declaredType);
    }

    @Override
    public DictionaryContext<T> withoutName() {
        return new DictionaryContext<>(Option.none(), descriptor, keyContext, entryContext, declaredType);
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
    public Iterable<XmlElement> toXml(Object object) {
        @SuppressWarnings("unchecked")
        T value = (T) object;

        Iterator<Tuple2<?, ?>> entries = Iterator.ofAll(descriptor.entries(value));
        var elements = entries
                .flatMap(t -> Iterator
                        .ofAll(entryContext.toXml(t._2))
                        .map(u -> ((XmlElement) u).addUnits(keyContext.toXml(t._1))));

        return nameOpt.isDefined()
                ? Option.of(XmlElement.of(nameOpt.get()).withChildren(elements))
                : elements;
    }

    @Override
    public T ofXml(XmlElement xml) {
        if (nameOpt.isDefined()) {
            Option<XmlElement> element = xml.elements().find(e -> Objects.equals(nameOpt.get(), e.name()));
            if (element.isEmpty()) {
                return descriptor.ofKeyEntries(Array.empty());
            }
            xml = element.get();
        }

        Seq<XmlElement> children = xml.elements();
        String entryName = entryContext.name();
        if (Strings.notEmpty(entryName)) {
            children = children.filter(e -> Objects.equals(entryName, e.name()));
        }

        Seq<Tuple2<?, ?>> value = Seq.narrow(children
                .map(e -> Tuple.of(keyContext.ofXml(e), entryContext.ofXml(e)))
                .filter(t -> (null != t._1 && null != t._2)));

        return descriptor.ofKeyEntries(declaredType, value);
    }

}
