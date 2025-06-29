package io.openmapper.recordxml.v3.schema;

import java.lang.reflect.Type;

import io.openmapper.recordxml.util.Java;
import io.openmapper.recordxml.v3.config.Configuration;
import io.openmapper.recordxml.v3.context.*;
import io.openmapper.recordxml.v3.descriptor.*;
import io.vavr.control.Option;

public record Schema(Provider<PrimitiveDescriptor<?>> primitives,
                     Provider<StructureDescriptor<?>> structures,
                     Provider<PolymorphicDescriptor<?>> polymorphics,
                     Provider<PresenceDescriptor<?>> presences,
                     Provider<SequenceDescriptor<?>> sequences,
                     Provider<DictionaryDescriptor<?>> dictionaries) {

    static final String FRAGMENT = ":fragment";

    public static Schema of(Configuration configuration) {
        return new Schema(
                Provider.of(configuration.primitives()),
                Provider.of(configuration.structures()),
                Provider.of(configuration.polymorphics()),
                Provider.of(configuration.presenses()),
                Provider.of(configuration.sequences()),
                Provider.of(configuration.dictionaries()));
    }


    public <T> Context<T> context(boolean forceElement, Option<String> name, Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        Option<PrimitiveDescriptor<?>> primitive = primitives.find(rawClass);
        if (primitive.isDefined()) {
            return (PrimitiveContext<T>) PrimitiveContext.of(forceElement, name.getOrElse(FRAGMENT), primitive.get());
        }

        Option<PolymorphicDescriptor<?>> polymorphic = polymorphics.find(rawClass);
        if (primitive.isDefined()) {
            return (PolymorphicContext<T>) PolymorphicContext.of(this, polymorphic.get(), declaredType);
        }

        Option<StructureDescriptor<?>> structure = structures.find(rawClass);
        if (primitive.isDefined()) {
            return (StructureContext<T>) StructureContext.of(this, name, structure.get(), declaredType);
        }

        throw new IllegalArgumentException("No context descriptor for type: " + rawClass.getName());
    }

    public <T> ContainerContext<T> container(boolean forceElement, String name, Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        Option<SequenceDescriptor<?>> sequence = sequences.find(rawClass);
        if (sequence.isDefined()) {
            return (SequenceContext<T>) SequenceContext.of(this, name, sequence.get(), declaredType);
        }
        Option<DictionaryDescriptor<?>> dictionary = dictionaries.find(rawClass);
        if (dictionary.isDefined()) {
            return (DictionaryContext<T>) DictionaryContext.of(this, name, dictionary.get(), declaredType);
        }
        Option<PresenceDescriptor<?>> presence = presences.find(rawClass);
        if (presence.isDefined()) {
            return (PresenceContext<T>) PresenceContext.of(this, forceElement, name, presence.get(), declaredType);
        }
        throw new IllegalArgumentException("No presence descriptor for type: " + rawClass.getName());
    }
}
