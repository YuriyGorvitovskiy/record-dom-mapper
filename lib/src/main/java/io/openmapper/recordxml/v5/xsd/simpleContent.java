package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.TypeName;
import io.openmapper.recordxml.v5.XmlNameSpace;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

@XmlNameSpace(SCHEMA)
public record simpleContent(@XmlNameSpace(SCHEMA) simpleExtensionType extension) implements particle {

    public static simpleContent of(TypeName simpleType, attribute... attributes) {
        return new simpleContent(simpleExtensionType.of(simpleType, attributes));
    }
}
