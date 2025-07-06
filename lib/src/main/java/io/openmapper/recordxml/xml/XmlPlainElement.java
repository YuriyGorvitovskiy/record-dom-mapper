package io.openmapper.recordxml.xml;

import java.security.InvalidParameterException;

import io.vavr.collection.Array;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public record XmlPlainElement(Map<String, String> attributes,
                              Seq<XmlNode> children) {
    public static XmlPlainElement ofUnits(Seq<XmlUnit> units) {
        return empty().addUnits(units);
    }

    public XmlPlainElement addUnits(Seq<XmlUnit> units) {
        var attributesAndNodes = units.partition(unit -> unit instanceof XmlAttribute);
        return this
                .addAttributes(attributesAndNodes._1.map(unit -> (XmlAttribute) unit))
                .addChildren(attributesAndNodes._2.map(unit -> (XmlNode) unit));
    }

    public static XmlPlainElement empty() {
        return new XmlPlainElement(LinkedHashMap.empty(), Array.empty());
    }

    public XmlPlainElement addChildren(Seq<? extends XmlNode> nodes) {
        return new XmlPlainElement(attributes, children.appendAll(nodes));
    }

    public XmlPlainElement addAttributes(Seq<XmlAttribute> attributes) {

        return attributes.isEmpty()
                ? this
                : new XmlPlainElement(
                attributes.foldLeft(
                        this.attributes,
                        (m, a) -> m.put(
                                a.name(),
                                a.value(),
                                (v, u) -> {
                                    throw new InvalidParameterException("Attribute name " + a.name() + " has a conflict on the element");
                                })),
                children);
    }

    public XmlElement name(String name) {
        return new XmlElement(name, attributes, children);
    }

    public XmlPlainElement addChildren(XmlNode... nodes) {
        return addChildren(Array.of(nodes));
    }

    public XmlPlainElement addAttribute(String key, String value) {
        return new XmlPlainElement(attributes.put(key, value), children);
    }


}
