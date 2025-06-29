package io.openmapper.recordxml.v3.context;

import java.lang.reflect.Type;

import io.openmapper.recordxml.util.Strings;
import io.openmapper.recordxml.v3.descriptor.PrimitiveDescriptor;
import io.openmapper.recordxml.xml.XmlAttribute;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlText;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.control.Option;

public record PrimitiveContext<T>(boolean element, String name,
                                  PrimitiveDescriptor<T> descriptor,
                                  Type declaredType) implements Context<T> {

    public static <T> PrimitiveContext<T> of(boolean element, String name, PrimitiveDescriptor<T> descriptor, Type declaredType) {
        return new PrimitiveContext<>(element, name, descriptor, declaredType);
    }

    @Override
    public Iterable<? extends XmlUnit> toXml(Object object) {
        @SuppressWarnings("unchecked")
        T value = (T) object;

        String xmlString = descriptor.toXml(value);
        return Option.of(element
                ? XmlElement.of(name).withChildren(XmlText.of(xmlString))
                : XmlAttribute.of(name, xmlString));
    }

    @Override
    public T ofXml(XmlElement xml) {
        if (element) {
            return xml.attributes()
                    .get(name)
                    .map(a -> descriptor.ofXml(declaredType, a.value()))
                    .getOrNull();
        }

        return descriptor.ofXml(
                declaredType,
                xml.children()
                        .map(c -> c instanceof XmlText t ? t.value() : Strings.EMPTY)
                        .mkString().trim());
    }

}
