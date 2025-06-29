package io.openmapper.recordxml.v3.context;

public sealed interface ContainerContext<T> extends Context<T> permits PresenceContext, SequenceContext, DictionaryContext {

    ContainerContext<T> withoutName();

    boolean isElement();
}
