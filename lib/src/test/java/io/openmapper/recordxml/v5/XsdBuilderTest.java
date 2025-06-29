package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.v5.config.ConfigImpl;
import io.openmapper.recordxml.xsd.*;
import io.openmapper.recordxml.xsd.XsdSimple.Predefined;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

sealed interface Base permits DerivedA, DerivedB {
}

record DerivedA(String name, Base recursive) implements Base {
}

record DerivedB(String name, Base recursive1, Base recursive2) implements Base {
}


public class XsdBuilderTest {

    @Test
    void simple() {
        // Setup
        record Simple(String name) {
        }
        XsdBuilder subject = new XsdBuilder(new ConfigImpl());

        // Execute
        XsdSchema result = subject.build("Root", Simple.class);

        // Verify
        XsdTypeRef simpleRef = XsdTypeRef.of("Simple");

        assertEquals(
                XsdSchema.empty("Root", simpleRef)
                        .add(XsdComplex.of(simpleRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))),
                result);
    }

    @Test
    void recursive() {
        // Setup
        record Simple(String name,
                      Simple recursive) {
        }

        XsdBuilder subject = new XsdBuilder(new ConfigImpl());

        // Execute
        XsdSchema result = subject.build("Root", Simple.class);

        // Verify
        XsdTypeRef simpleRef = XsdTypeRef.of("Simple");

        assertEquals(
                XsdSchema.empty("Root", simpleRef)
                        .add(XsdComplex.of(simpleRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(XsdElement.of("recursive", simpleRef))),
                result);
    }

    @Test
    void polymorphic() {
        // Setup
        XsdBuilder subject = new XsdBuilder(new ConfigImpl());

        // Execute
        XsdSchema result = subject.build("Root", Base.class);

        // Verify
        XsdTypeRef baseRef = XsdTypeRef.of("Base");
        XsdTypeRef derivedARef = XsdTypeRef.of("DerivedA");
        XsdTypeRef derivedBRef = XsdTypeRef.of("DerivedB");

        assertEquals(
                XsdSchema.empty("Root", baseRef)
                        .add(XsdComplex.of(baseRef)
                                .addElements(
                                        XsdElement.of("DerivedA", derivedARef),
                                        XsdElement.of("DerivedB", derivedBRef)))
                        .add(XsdComplex.of(derivedARef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(
                                        XsdElement.of("DerivedA", derivedARef),
                                        XsdElement.of("DerivedB", derivedBRef)))
                        .add(XsdComplex.of(derivedBRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(
                                        XsdElement.of("recursive1", baseRef),
                                        XsdElement.of("recursive2", baseRef))),
                result);
    }
}

