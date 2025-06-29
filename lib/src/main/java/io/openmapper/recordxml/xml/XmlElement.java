package io.openmapper.recordxml.xml;


import io.vavr.collection.*;
import org.w3c.dom.Document;

public record XmlElement(String name, Map<String, XmlAttribute> attributes, Seq<XmlNode> children) implements XmlNode {
    public static XmlElement of(String name) {
        return new XmlElement(name, LinkedHashMap.empty(), Array.empty());
    }

    public XmlElement withAttributes(Traversable<XmlAttribute> attributes) {
        return new XmlElement(
                name,
                attributes.toLinkedMap(XmlAttribute::name, a -> a),
                children);
    }

    public XmlElement addAttribute(String name, String xml) {
        return new XmlElement(
                name,
                attributes.put(name, XmlAttribute.of(name, xml)),
                children);
    }


    public XmlElement withChildren(XmlNode... children) {
        return withChildren(Array.of(children));
    }

    public XmlElement withChildren(Traversable<? extends XmlNode> children) {
        return new XmlElement(name, attributes, Array.ofAll(children));
    }


    @Override
    public org.w3c.dom.Element toDOM(Document document) {
        org.w3c.dom.Element element = document.createElement(name);
        attributes.forEach((k, v) -> element.setAttribute(k, v.value()));
        children.forEach(c -> element.appendChild(c.toDOM(document)));
        return element;
    }

    public XmlElement withUnits(Iterable<? extends XmlUnit> units) {
        var attributesAndNodes = Iterator.ofAll(units).partition(u -> u instanceof XmlAttribute);
        return new XmlElement(
                name,
                attributesAndNodes._1
                        .map(u -> (XmlAttribute) u)
                        .toMap(a -> a.name(), a -> a),
                attributesAndNodes._1
                        .map(u -> (XmlNode) u)
                        .toArray());
    }

    public XmlElement addUnits(Iterable<? extends XmlUnit> units) {
        var attributesAndNodes = Iterator.ofAll(units).partition(u -> u instanceof XmlAttribute);
        return new XmlElement(
                name,
                attributesAndNodes._1
                        .map(u -> (XmlAttribute) u)
                        .toMap(a -> a.name(), a -> a)
                        .merge(attributes),
                children.appendAll(attributesAndNodes._1
                        .map(u -> (XmlNode) u)));
    }

    public Seq<XmlElement> elements() {
        return children
                .filter(c -> c instanceof XmlElement)
                .map(c -> (XmlElement) c);
    }

    public String text() {
        return children
                .filter(c -> c instanceof XmlText)
                .map(c -> ((XmlText) c).value())
                .mkString()
                .trim();
    }
}
