package io.openmapper.recordxml.v3.context;

import java.lang.reflect.Type;

import io.openmapper.recordxml.util.Java;
import io.openmapper.recordxml.util.Strings;
import io.openmapper.recordxml.v3.descriptor.PolymorphicDescriptor;
import io.openmapper.recordxml.v3.schema.Schema;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import io.vavr.control.Option;


public record PolymorphicContext<T>(PolymorphicDescriptor<T> descriptor, Map<Class<?>, Context<?>> typedContexts,
                                    Map<String, Context<?>> namedContexts) implements Context<T> {

    public static <T> PolymorphicContext<T> of(Schema schema, PolymorphicDescriptor<T> descriptor, Type type) {
        Map<Class<?>, Context<?>> typedContexts = Iterator.ofAll(descriptor.concreteTypes(type))
                .toMap(Java::rawClass, t -> schema.context(true, Option.none(), t));

        Map<String, Context<?>> namedContexts = typedContexts.values().toMap(Context::name, c -> c);

        return new PolymorphicContext<>(descriptor, typedContexts, namedContexts);
    }

    @Override
    public String name() {
        return Strings.EMPTY;
    }

    @Override
    public Iterable<? extends XmlUnit> toXml(Object object) {
        @SuppressWarnings("unchecked")
        T value = (T) object;

        Object concreteValue = descriptor.toConcreteValue(value);
        Context<?> concreteContext = typedContexts
                .get(concreteValue.getClass())
                .getOrElseThrow(() -> new IllegalArgumentException("No context for concrete type: " + concreteValue.getClass()));

        return concreteContext.toXml(concreteValue);
    }

    @Override
    public T ofXml(XmlElement xml) {
        Object value = namedContexts.get(xml.name())
                .map(c -> c.ofXml(xml))
                .getOrNull();

        return descriptor.ofConcreteValue(value);
    }

}
