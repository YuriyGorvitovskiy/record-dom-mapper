package io.openmapper.recordxml.v3.config;

import io.openmapper.recordxml.v3.descriptor.*;
import io.vavr.collection.Seq;

public record Configuration(Seq<PrimitiveDescriptor<?>> primitives,
                            Seq<StructureDescriptor<?>> structures,
                            Seq<PolymorphicDescriptor<?>> polymorphics,
                            Seq<PresenceDescriptor<?>> presenses,
                            Seq<SequenceDescriptor<?>> sequences,
                            Seq<DictionaryDescriptor<?>> dictionaries) {

}
