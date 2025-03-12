package io.openmapper.recordxml;

import io.vavr.Tuple;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Either;
import io.vavr.control.Option;

record VavrSerializer() implements ContainerSerializer {

    public static VavrSerializer stock() {
        return new VavrSerializer();
    }

    @Override
    public Option<Traversable<?>> getEntries(Object container) {
        return switch (container) {
            case Traversable<?> traversable -> Option.of(traversable);
            case Option<?> option -> Option.of(option.toStream());
            case Tuple tuple -> Option.of(tuple.toSeq());
            case Either<?,?> either -> Option.of(either.toStream());
            case null -> Option.of(Stream.empty());
            default -> Option.none();
        };
    }
}
