package io.openmapper.recordxml;

public sealed interface Node extends Unit permits Element, Text, Comment {

    org.w3c.dom.Node toDOM(org.w3c.dom.Document document);
}
