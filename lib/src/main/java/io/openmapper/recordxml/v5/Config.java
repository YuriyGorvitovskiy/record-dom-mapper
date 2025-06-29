package io.openmapper.recordxml.v5;

import java.lang.reflect.Type;

public interface Config {
    MappingType select(Type declaredType);
}
