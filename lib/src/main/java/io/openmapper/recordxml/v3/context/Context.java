package io.openmapper.recordxml.v3.context;

import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;

public sealed interface Context<T> permits PrimitiveContext, ContainerContext, PolymorphicContext, StructureContext {
    String name();

    Iterable<? extends XmlUnit> toXml(Object value);

    T ofXml(XmlElement xml);
}
