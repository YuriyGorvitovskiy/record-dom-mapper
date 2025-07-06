package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

public interface MappingResolver {
    MappingType resolveType(Type declaredType);
}
