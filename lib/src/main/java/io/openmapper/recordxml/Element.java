package io.openmapper.recordxml;


import io.vavr.collection.*;
import org.w3c.dom.Document;

public record Element(String name, Map<String, Attribute> attributes, Seq<Node> children) implements Node {
    public static Element of(String name) {
        return new Element(name, LinkedHashMap.empty(), Array.empty());
    }

    public Element withAttributes(Traversable<Attribute> attributes) {
        return new Element(
                name,
                attributes.toLinkedMap(Attribute::name, a->a),
                children);
    }

    public Element withChildren(Node... children) {
        return withChildren(Array.of(children));
    }

    public Element withChildren(Traversable<Node> children) {
        return new Element(name, attributes, Array.ofAll(children));
    }


    @Override
    public org.w3c.dom.Element toDOM(Document document) {
        org.w3c.dom.Element element = document.createElement(name);
        attributes.forEach((k,v)->element.setAttribute(k, v.value()));
        children.forEach(c->element.appendChild(c.toDOM(document)));
        return element;
    }
}
