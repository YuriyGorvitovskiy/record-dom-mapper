package io.openmapper.recordxml.v4.config;

public sealed interface Descriptor<T> permits PrimitiveDescriptor, ContainerDescriptor, PolymorphicDescriptor, StructureDescriptor {
    Selector<T> selector();
}
