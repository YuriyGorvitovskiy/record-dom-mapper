package io.openmapper.recordxml.v5.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.openmapper.recordxml.v5.*;

public interface MapMapper {

    static Mapper of(Config config, Type declaredType) {
        if (!(declaredType instanceof ParameterizedType parametrizedType)) {
            throw new IllegalArgumentException("The declared type must be a parameterized type");
        }
        Mapper keyMapper = config.mapperFor(parametrizedType.getActualTypeArguments()[0]);
        Mapper valueMapper = config.mapperFor(parametrizedType.getActualTypeArguments()[1]);

        if (!(keyMapper instanceof SimpleMapper keySimpleMapper)) {
            throw new IllegalArgumentException("The key mapper must be a simple type");
        }

        return switch (valueMapper) {
            case ChoiceMapper choiceMapper -> new MapChoiceMapper(keySimpleMapper, choiceMapper);
            case ComplexMapper complexMapper -> new MapComplexMapper(keySimpleMapper, complexMapper);
            case SimpleMapper simpleMapper -> new MapSimpleMapper(keySimpleMapper, simpleMapper);
            case EmbeddedMapper ignored -> throw new RuntimeException("Embedded Mapper does not supported for map");
            case SequenceMapper ignored -> throw new RuntimeException("Sequence Mapper does not supported for map");
        };
    }
}
