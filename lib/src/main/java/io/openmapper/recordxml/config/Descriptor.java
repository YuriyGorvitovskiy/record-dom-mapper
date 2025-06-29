package io.openmapper.recordxml.config;

public sealed interface Descriptor permits SimpleDescriptor, ContainerDescriptor, PolymorphicDescriptor, ComplexDescriptor {
    Selector selector();
}
