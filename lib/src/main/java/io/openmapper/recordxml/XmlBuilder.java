package io.openmapper.recordxml;

import io.vavr.collection.Array;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.w3c.dom.*;

public sealed interface XmlBuilder<N extends Node>  permits NoneBuilder, TextBuilder, AttributeBuilder, ElementBuilder {


    Option<N> build(Document document, String text);
    Traversable<? extends N> build(Document document, Traversable<? extends Node> children);

    @SuppressWarnings("unused")
    static XmlBuilder<Node> none() {
        return NoneBuilder.NONE;
    }

    @SuppressWarnings("unused")
    static XmlBuilder<Text> text()  {
        return TextBuilder.TEXT;
    }

    @SuppressWarnings("unused")
    static XmlBuilder<Attr> attribute(String name)  {
        return new AttributeBuilder(name);
    }

    static XmlBuilder<Element> element(String name)  {
        return new ElementBuilder(name);
    }
}

enum NoneBuilder implements XmlBuilder<Node> {
    NONE;

    @Override
    public Option<Node> build(Document document, String text) {
        return Option.none();
    }

    public Traversable<? extends Node> build(Document document, Traversable<? extends Node> children) {
        return children;
    }

}

enum TextBuilder implements XmlBuilder<Text>{

    TEXT;

    @Override
    public Option<Text> build(Document document, String text) {
        return Option.when(null != text , ()-> document.createTextNode(text));
    }

    public Traversable<? extends Text> build(Document document, Traversable<? extends Node> children) {
        throw new UnsupportedOperationException("TextBuilder can't build children");
    }

}

record ElementBuilder(String name) implements XmlBuilder<Element> {

    @Override
    public Option<Element>  build(Document document, String text) {
        return Option.when(null != text , ()-> {
            Element element = document.createElement(name);
            Text child = document.createTextNode(text);
            element.appendChild(child);
            return element;
        });
    }

    public Traversable<? extends Element> build(Document document, Traversable<? extends Node> children) {
        Element element = document.createElement(name);
        children.forEach(element::appendChild);
        return Array.of(element);
    }

}

record AttributeBuilder(String name) implements XmlBuilder<Attr> {

    @Override
    public Option<Attr>  build(Document document,String text) {
        return Option.when(null != text , ()-> {
            Attr attribute = document.createAttribute(name);
            attribute.setValue(text);
            return attribute;
        });
    }

    public Traversable<? extends Attr> build(Document document, Traversable<? extends Node> children) {
        throw new UnsupportedOperationException("TextBuilder can't build children");
    }

}

