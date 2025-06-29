package io.openmapper.recordxml.xml;


public sealed interface XmlNode extends XmlUnit permits XmlElement, XmlText, XmlComment {

    org.w3c.dom.Node toDOM(org.w3c.dom.Document document);
}
