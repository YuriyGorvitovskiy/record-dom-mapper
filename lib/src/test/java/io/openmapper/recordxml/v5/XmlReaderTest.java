package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.XmlUtils;
import io.openmapper.recordxml.util.Strings;
import io.openmapper.recordxml.v5.config.ConfigImpl;
import io.openmapper.recordxml.xml.XmlElement;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlReaderTest {

    @Test
    void simple() throws Exception {
        // Setup
        String xml = Strings.resource(XmlReaderTest.class, "Simple.xml");
        XmlElement root = XmlElement.of(XmlUtils.ofXml(xml));
        XmlReader subject = new XmlReader(ConfigImpl.DEFAULT);

        // Execute
        Simple result = subject.ofXml(Simple.class, root);

        // Verify
        assertEquals(
                new Simple("test1",
                        HashMap.<String, String>empty()
                                .put("one", "first")
                                .put("two", "second")),
                result);
    }

    @Test
    void recursive() throws Exception {
        // Setup
        String xml = Strings.resource(XmlReaderTest.class, "Recursive.xml");
        XmlElement root = XmlElement.of(XmlUtils.ofXml(xml));
        XmlReader subject = new XmlReader(ConfigImpl.DEFAULT);

        // Execute
        Recursive result = subject.ofXml(Recursive.class, root);

        // Verify
        assertEquals(
                new Recursive("test1",
                        new Recursive("test2",
                                new Recursive("test3", null))),
                result);
    }

    @Test
    void recursiveMap() throws Exception {
        // Setup
        String xml = Strings.resource(XmlReaderTest.class, "RecursiveMap.xml");
        XmlElement root = XmlElement.of(XmlUtils.ofXml(xml));
        XmlReader subject = new XmlReader(ConfigImpl.DEFAULT);

        // Execute
        RecursiveMap result = subject.ofXml(RecursiveMap.class, root);

        // Verify
        assertEquals(
                new RecursiveMap("test1",
                        HashMap.<String, RecursiveMap>empty()
                                .put("one", new RecursiveMap("test2-1", HashMap.of("three", new RecursiveMap("test3", HashMap.empty()))))
                                .put("two", new RecursiveMap("test2-2", HashMap.empty()))),
                result);
    }

    @Test
    void polymorphic() throws Exception {
        // Setup
        String xml = Strings.resource(XmlReaderTest.class, "Polymorphic.xml");
        XmlElement root = XmlElement.of(XmlUtils.ofXml(xml));
        XmlReader subject = new XmlReader(ConfigImpl.DEFAULT);

        // Execute
        Base result = subject.ofXml(DerivedC.class, root);

        // Verify
        assertEquals(
                new DerivedC("1",
                        new DerivedA("S1", new DerivedB("S1-1", HashMap.empty())),
                        HashMap.<String, Base>empty()
                                .put("one", new DerivedB("P1",
                                        HashMap.<String, Base>empty()
                                                .put("one", new DerivedA("P1-1", null))
                                                .put("two", new DerivedB("P1-2",
                                                        HashMap.<String, Base>empty()
                                                                .put("one", new DerivedA("P1-2-1", null))
                                                                .put("two", new DerivedB("P1-2-2", HashMap.empty()))
                                                                .put("three", new DerivedC(null, null, null))))
                                                .put("three",
                                                        new DerivedC("P1-3",
                                                                new DerivedB("P1-3-S1", HashMap.empty()),
                                                                HashMap.<String, Base>empty()
                                                                        .put("one", new DerivedA("P1-3-P1", null))))))
                                .put("two", new DerivedC("P2", null, null))
                                .put("three", new DerivedA("P3",
                                        new DerivedC("P3-1", null, null)))),
                result);
    }
}

