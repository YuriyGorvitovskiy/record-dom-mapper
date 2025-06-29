package io.openmapper.recordxml.v3;

import io.openmapper.recordxml.v3.config.Configuration;
import io.openmapper.recordxml.v3.context.Context;
import io.openmapper.recordxml.v3.schema.Schema;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.control.Option;


public record XmlMapper(Configuration configuration) {

    static final String KEY = ":key";

    public XmlElement toXml(Object value) {
        Seq<? extends XmlUnit> units = toXmlUnits(value);
        if (units.size() != 1) {
            throw new IllegalArgumentException("Not a single element");
        }
        XmlUnit unit = units.get();
        if (!(unit instanceof XmlElement element)) {
            throw new IllegalArgumentException("Not an element");
        }
        return element;
    }

    public Seq<? extends XmlUnit> toXmlUnits(Object value) {
        Class<?> clazz = value.getClass();
        Schema schema = Schema.of(configuration);
        Context<?> context = schema.context(false, Option.none(), clazz);
        return Array.ofAll(context.toXml(value));
    }

    public <T> T ofXml(Class<T> clazz, XmlElement xml) {
        Schema schema = Schema.of(configuration);
        Context<T> context = schema.context(false, Option.none(), clazz);
        return context.ofXml(xml);
    }

}
