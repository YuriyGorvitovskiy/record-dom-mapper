package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

public interface XsdResolver {
    MappingType resolveType(Type declaredType);
}
