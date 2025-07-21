package io.openmapper.recordxml.v5.config;

import java.util.Objects;

import io.openmapper.recordxml.v5.SimpleMapper;
import io.openmapper.recordxml.v5.TypeName;
import io.openmapper.recordxml.v5.XsdEntry;

public record StringMapper() implements SimpleMapper {

    @Override
    public String toXml(Object value) {
        return Objects.toString(value);
    }

    @Override
    public Object ofXml(String xml) {
        return xml;
    }

    @Override
    public XsdEntry<TypeName> xsd() {
        return XsdEntry.simple("xs:string");
    }
}
