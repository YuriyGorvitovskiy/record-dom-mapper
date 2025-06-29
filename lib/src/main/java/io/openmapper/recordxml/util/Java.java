package io.openmapper.recordxml.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;

public interface Java {

    static Class<?> rawClass(Type type) {
        return switch (type) {
            case Class<?> c -> c;
            case ParameterizedType p -> (Class<?>) p.getRawType();
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    static Seq<Class<?>> collectPermittedRecords(Class<?> sealedInterface) {
        var recordsAndInterfaces = Array.of(sealedInterface.getPermittedSubclasses())
                .partition(Record.class::isAssignableFrom);

        return recordsAndInterfaces._1.appendAll(recordsAndInterfaces._2
                .flatMap(Java::collectPermittedRecords));
    }

}
