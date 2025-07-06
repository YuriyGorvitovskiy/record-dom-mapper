package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.v5.config.ConfigImpl;
import io.openmapper.recordxml.xsd.*;
import io.openmapper.recordxml.xsd.XsdSimple.Predefined;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XsdBuilderTest {

    @Test
    void simple() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        XsdSchema result = subject.build("Root", Simple.class);

        // Verify
        XsdTypeRef simpleRef = XsdTypeRef.of("Simple");
        XsdTypeRef mapRef = XsdTypeRef.of("string_MappedBy_string");

        assertEquals(
                XsdSchema.empty("Root", simpleRef)
                        .add(XsdComplex.of(simpleRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(XsdElement.of("map", mapRef)))
                        .add(XsdComplex.of(mapRef)
                                .extensionOf(Predefined.STRING.ref())
                                .addAttributes(XsdAttribute.of("Key", Predefined.STRING.ref()))),
                result);
    }

    @Test
    void recursive() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        XsdSchema result = subject.build("Root", Recursive.class);

        // Verify
        XsdTypeRef simpleRef = XsdTypeRef.of("Recursive");

        assertEquals(
                XsdSchema.empty("Root", simpleRef)
                        .add(XsdComplex.of(simpleRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(XsdElement.of("recursive", simpleRef))),
                result);
    }

    @Test
    void recursiveMap() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        XsdSchema result = subject.build("Root", RecursiveMap.class);

        // Verify
        XsdTypeRef simpleRef = XsdTypeRef.of("RecursiveMap");
        XsdTypeRef simpleMapRef = XsdTypeRef.of("RecursiveMap_MappedBy_string");

        assertEquals(
                XsdSchema.empty("Root", simpleRef)
                        .add(XsdComplex.of(simpleRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(XsdElement.of("recursive", simpleMapRef)))
                        .add(XsdComplex.of(simpleMapRef)
                                .extensionOf(simpleRef)
                                .addAttributes(XsdAttribute.of("Key", Predefined.STRING.ref()))),
                result);
    }

    @Test
    void polymorphic() {
        // Setup
        XsdBuilder subject = new XsdBuilder(ConfigImpl.DEFAULT);

        // Execute
        XsdSchema result = subject.build("Root", Base.class);

        // Verify
        XsdTypeRef baseRef = XsdTypeRef.of("Base");
        XsdTypeRef derivedARef = XsdTypeRef.of("DerivedA");
        XsdTypeRef derivedBRef = XsdTypeRef.of("DerivedB");
        XsdTypeRef derivedCRef = XsdTypeRef.of("DerivedC");
        XsdTypeRef baseMapRef = XsdTypeRef.of("Base_MappedBy_string");
        XsdTypeRef derivedAMapRef = XsdTypeRef.of("DerivedA_MappedBy_string");
        XsdTypeRef derivedBMapRef = XsdTypeRef.of("DerivedB_MappedBy_string");
        XsdTypeRef derivedCMapRef = XsdTypeRef.of("DerivedC_MappedBy_string");


        assertEquals(
                XsdSchema.empty("Root", baseRef)
                        .add(XsdComplex.of(baseRef)
                                .addElements(
                                        XsdElement.of("DerivedA", derivedARef),
                                        XsdElement.of("DerivedB", derivedBRef),
                                        XsdElement.of("DerivedC", derivedCRef)))
                        .add(XsdComplex.of(derivedARef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(
                                        XsdElement.of("DerivedA", derivedARef),
                                        XsdElement.of("DerivedB", derivedBRef),
                                        XsdElement.of("DerivedC", derivedCRef)))
                        .add(XsdComplex.of(derivedBRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(
                                        XsdElement.of("DerivedA", derivedAMapRef),
                                        XsdElement.of("DerivedB", derivedBMapRef),
                                        XsdElement.of("DerivedC", derivedCMapRef)))
                        .add(XsdComplex.of(derivedCRef)
                                .addAttributes(XsdAttribute.of("name", Predefined.STRING.ref()))
                                .addElements(
                                        XsdElement.of("recursive", baseRef),
                                        XsdElement.of("recursiveMap", baseMapRef)))
                        .add(XsdComplex.of(baseMapRef)
                                .addElements(
                                        XsdElement.of("DerivedA", derivedAMapRef),
                                        XsdElement.of("DerivedB", derivedBMapRef),
                                        XsdElement.of("DerivedC", derivedCMapRef)))
                        .add(XsdComplex.of(derivedAMapRef)
                                .extensionOf(derivedARef)
                                .addAttributes(XsdAttribute.of("Key", Predefined.STRING.ref())))
                        .add(XsdComplex.of(derivedBMapRef)
                                .extensionOf(derivedBRef)
                                .addAttributes(XsdAttribute.of("Key", Predefined.STRING.ref())))
                        .add(XsdComplex.of(derivedCMapRef)
                                .extensionOf(derivedCRef)
                                .addAttributes(XsdAttribute.of("Key", Predefined.STRING.ref()))),
                result);
    }
}
