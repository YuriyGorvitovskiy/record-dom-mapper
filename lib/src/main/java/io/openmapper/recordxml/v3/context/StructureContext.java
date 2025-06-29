package io.openmapper.recordxml.v3.context;

import java.lang.reflect.Type;

import io.openmapper.recordxml.v3.descriptor.StructureDescriptor;
import io.openmapper.recordxml.v3.descriptor.StructureDescriptor.Field;
import io.openmapper.recordxml.v3.schema.Schema;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record StructureContext<T>(String name,
                                  StructureDescriptor<T> descriptor,
                                  Seq<Entry> entries,
                                  Type declaredType) implements Context<T> {


    public static <T> StructureContext<T> of(Schema schema, Option<String> name, StructureDescriptor<T> descriptor, Type declaredType) {
        Seq<Entry> entries = Iterator.ofAll(descriptor.fields())
                .map(f -> Entry.of(schema, f))
                .toArray();
        if (entries.count(Entry::isElement) == 1) {
            entries = entries.map(Entry::withoutName);
        }

        return new StructureContext<>(
                name.getOrElse(descriptor.xmlName()),
                descriptor,
                entries,
                declaredType);
    }


    @Override
    public Iterable<XmlElement> toXml(Object object) {
        @SuppressWarnings("unchecked")
        T value = (T) object;

        var units = entries.flatMap(e -> e.toXml(descriptor, value));
        return Option.of(XmlElement.of(name).withUnits(units));
    }

    @Override
    public T ofXml(XmlElement xml) {
        var values = entries.map(e -> e.context().ofXml(xml));
        return descriptor.ofFields(declaredType, values);
    }
}

record Entry(Field field, ContainerContext<?> context) {

    static Entry of(Schema schema, Field field) {
        ContainerContext<?> context = schema.container(false, field.xmlName(), field.genericType());
        return new Entry(field, context);
    }

    Entry withoutName() {
        return isElement()
                ? new Entry(field, context.withoutName())
                : this;
    }

    boolean isElement() {
        return context.isElement();
    }

    <P> Iterable<? extends XmlUnit> toXml(StructureDescriptor<P> parentDescriptor, P parentValue) {
        return context.toXml(parentDescriptor.fieldValue(parentValue, field));
    }

}

