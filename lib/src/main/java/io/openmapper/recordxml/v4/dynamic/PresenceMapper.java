package io.openmapper.recordxml.v4.dynamic;

import io.vavr.control.Option;

public interface PresenceMapper<P, E> {

    Option<E> toOption(P value);

    P ofOption(Option<E> element);

}
