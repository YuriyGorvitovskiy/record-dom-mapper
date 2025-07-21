package io.openmapper.recordxml.v5.xsd;

import io.openmapper.recordxml.v5.TypeName;
import io.openmapper.recordxml.v5.XmlNameSpace;

import static io.openmapper.recordxml.v5.XmlNameSpace.SCHEMA;

@XmlNameSpace(SCHEMA)
public record complexContent(@XmlNameSpace(SCHEMA) complexExtensionType extension) implements particle {

    public static complexContent of(TypeName complexType, attribute... attributes) {
        return new complexContent(complexExtensionType.of(complexType, attributes));
    }

}
