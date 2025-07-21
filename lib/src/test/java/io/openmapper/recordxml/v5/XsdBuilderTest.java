package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.XmlUtils;
import io.openmapper.recordxml.util.SoftenEx;
import io.openmapper.recordxml.util.Strings;
import io.openmapper.recordxml.v5.config.ConfigImpl;
import io.openmapper.recordxml.v5.xsd.schema;
import io.openmapper.recordxml.xml.XmlElement;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XsdBuilderTest {

    @Test
    void simple() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        schema result = subject.build("Test", Simple.class);

        // Verify
        String expected = Strings.resource(XsdBuilderTest.class, "Simple.xsd");
        String actual = toXsd(result);
        assertEquals(expected, actual);
    }

    public static String toXsd(schema schema) {
        XmlElement root = new XmlWriter(ConfigImpl.DEFAULT)
                .toXml("xs:schema", schema);

        Document doc = SoftenEx.call(() -> XmlUtils.builder().newDocument());
        doc.appendChild(root.toDOM(doc));

        return SoftenEx.call(() -> XmlUtils.toXml(doc));
    }

    @Test
    void recursive() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        schema result = subject.build("Test", Recursive.class);

        // Verify
        String expected = Strings.resource(XsdBuilderTest.class, "Recursive.xsd");
        String actual = toXsd(result);
        assertEquals(expected, actual);
    }

    @Test
    void recursiveMap() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        schema result = subject.build("Test", RecursiveMap.class);

        // Verify
        String expected = Strings.resource(XsdBuilderTest.class, "RecursiveMap.xsd");
        String actual = toXsd(result);
        assertEquals(expected, actual);
    }

    @Test
    void polymorphic() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        schema result = subject.build("Test", DerivedC.class);

        // Verify
        String expected = Strings.resource(XsdBuilderTest.class, "Polymorphic.xsd");
        String actual = toXsd(result);
        assertEquals(expected, actual);
    }
}
