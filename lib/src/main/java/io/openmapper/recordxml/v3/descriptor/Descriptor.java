package io.openmapper.recordxml.v3.descriptor;

public sealed interface Descriptor<T> permits PrimitiveDescriptor, PresenceDescriptor, SequenceDescriptor, DictionaryDescriptor, PolymorphicDescriptor, StructureDescriptor {
    Selector<T> selector();
}
