package io.openmapper.recordxml.v5;

import io.openmapper.recordxml.v5.config.ConfigImpl;
import io.openmapper.recordxml.xsd.XsdSchema;
import io.openmapper.recordxml.xsd.XsdTypeRef;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(XsdSchema.empty("Root", XsdTypeRef.of("Simple")), result);
    }
}
