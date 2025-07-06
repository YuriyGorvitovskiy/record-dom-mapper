package io.openmapper.recordxml.v5;

public sealed interface Mapper permits SimpleMapper, EmbeddedMapper, ComplexMapper, SequenceMapper, ChoiceMapper {
}
