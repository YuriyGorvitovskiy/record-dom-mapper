package io.openmapper.recordxml.v3.schema;

import io.openmapper.recordxml.v3.descriptor.Descriptor;
import io.openmapper.recordxml.v3.descriptor.Selector;
import io.openmapper.recordxml.v3.descriptor.Selector.Match;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record Provider<D extends Descriptor<?>>(Map<Class<?>, D> explicit,
                                                Seq<D> polymorphic) {
    public static <D extends Descriptor<?>> Provider<D> of(Seq<D> descriptors) {
        Map<Match, Seq<D>> groupsBySelector = Map.narrow(descriptors
                .groupBy(d -> d.selector().match()));

        return new Provider<>(
                groupsBySelector.get(Match.EXACT)
                        .getOrElse(Array.empty())
                        .toMap(d -> d.selector().clazz(), d -> d),
                groupsBySelector.get(Selector.Match.SUPER)
                        .getOrElse(Array.empty()));
    }

    public Option<D> find(Class<?> type) {
        return explicit.get(type)
                .orElse(() -> polymorphic
                        .find(d -> d.selector().test(type)));
    }
}
