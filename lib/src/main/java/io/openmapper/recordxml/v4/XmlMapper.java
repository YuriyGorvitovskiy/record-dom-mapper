package io.openmapper.recordxml.v4;

import io.openmapper.recordxml.v4.schema.Schema;
import io.openmapper.recordxml.xml.XmlElement;

public class XmlMapper {

    public static <T> T ofXml(Class<T> rawType, XmlElement root) {
        Schema schema = Schema.of(rawType);
        return (T) schema.root(rawType).ofXml(root);
    }

    public static XmlElement toXml(Object value) {
        Class<?> rawType = value.getClass();
        Schema schema = Schema.of(rawType);
        return schema.root(rawType).toXml(value);
    }


}
