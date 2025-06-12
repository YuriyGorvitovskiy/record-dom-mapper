package io.openmapper.recordxml;

import io.vavr.control.Option;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecordDomMapperWithAllPrimitivesTest {

    // Example record capturing all supported primitives and fields
    public record AllPrimitivesRecord(
        String stringField,
        boolean booleanField,
        byte byteField,
        short shortField,
        int intField,
        long longField,
        float floatField,
        double doubleField,
        Date dateField,              // ISO-8601 serialization
        Instant instantField,        // ISO-8601 serialization
        SomeEnum enumField,          // name of enum serialized
        Option<String> optionalField // Optional field
    ) {}

    // Supported Enum for testing
    public enum SomeEnum {
        ENUM_VALUE_1,
        ENUM_VALUE_2
    }

    Mapper subject = Mapper.stock();

    /**
     * Test serialization of all supported primitives (including optional fields)
     */
    @Test
    void recordToXml_optionalFieldPresent() throws Exception {
        // Arrange
        AllPrimitivesRecord record = new AllPrimitivesRecord(
            "example",
            true,
            (byte) 1,
            (short) 10,
            42,
            1000L,
            3.14f,
            6.28d,
            new Date(1697040000123L), // Oct 11, 2023 (example)
            Instant.parse("2023-10-11T10:00:00.123Z"),
            SomeEnum.ENUM_VALUE_1,
            Option.some("optional-value")
        );

        // Act
        Document document = subject.recordToXml(XmlUtils.builder(), record);
        String result = XmlUtils.toXml(document);

        // Assert
        String expectedXml = """
                <AllPrimitivesRecord booleanField="true" byteField="1" dateField="2023-10-11T16:00:00.123Z" doubleField="6.28" enumField="ENUM_VALUE_1" floatField="3.14" instantField="2023-10-11T10:00:00.123Z" intField="42" longField="1000" optionalField="optional-value" shortField="10" stringField="example"/>""";
        assertEquals(expectedXml.strip(), result.strip(), "Serialized XML does not match!");
    }

    /**
     * Test deserialization of all supported primitives (including optional fields)
     */
    @Test
    void xmlToRecord_optionalFieldPresent() throws Exception {
        // Arrange
        String sourceXml = """
                <AllPrimitivesRecord booleanField="true"
                                     byteField="1"
                                     dateField="2023-10-11T16:00:00.123Z"
                                     doubleField="6.28"
                                     enumField="ENUM_VALUE_1"
                                     floatField="3.14"
                                     instantField="2023-10-11T10:00:00.123Z"
                                     intField="42"
                                     longField="1000"
                                     optionalField="optional-value"
                                     shortField="10"
                                     stringField="example" />""";

        Document document = XmlUtils.ofXml(sourceXml);

        // Act
        AllPrimitivesRecord record = subject.xmlToRecord(AllPrimitivesRecord.class, document);

        // Assert
        assertEquals("example", record.stringField());
        assertTrue(record.booleanField());
        assertEquals((byte) 1, record.byteField());
        assertEquals((short) 10, record.shortField());
        assertEquals(42, record.intField());
        assertEquals(1000L, record.longField());
        assertEquals(3.14f, record.floatField());
        assertEquals(6.28, record.doubleField(), 0.001);
        assertEquals(new Date(1697040000123L), record.dateField());
        assertEquals(Instant.parse("2023-10-11T10:00:00.123Z"), record.instantField());
        assertEquals(SomeEnum.ENUM_VALUE_1, record.enumField());
        assertEquals("optional-value", record.optionalField().get());
    }

    /**
     * Test serialization of all supported primitives with optional fields absent.
     */
    @Test
    void recordToXml_optionalFieldAbsent() throws Exception {
        // Arrange
        AllPrimitivesRecord record = new AllPrimitivesRecord(
                "example",
                true,
                (byte) 1,
                (short) 10,
                42,
                1000L,
                3.14f,
                6.28d,
                new Date(1697040000123L), // Oct 11, 2023 (example)
                Instant.parse("2023-10-11T10:00:00.123Z"),
                SomeEnum.ENUM_VALUE_2,
                Option.none() // Optional field is empty
        );

        // Act - Serialization
        Document document = subject.recordToXml(XmlUtils.builder(), record);
        String serializedXml = XmlUtils.toXml(document);

        // Assert - Serialized XML
        String expectedSerializedXml = """
                <AllPrimitivesRecord booleanField="true" byteField="1" dateField="2023-10-11T16:00:00.123Z" doubleField="6.28" enumField="ENUM_VALUE_2" floatField="3.14" instantField="2023-10-11T10:00:00.123Z" intField="42" longField="1000" shortField="10" stringField="example"/>""";
        assertEquals(expectedSerializedXml.strip(), serializedXml.strip(), "Serialized XML does not match!");
    }

    /**
     * Test deserialization of all supported primitives with optional fields absent.
     */
    @Test
    void xmlToRecord_optionalFieldAbsent() throws Exception {
        // Arrange
        String serializedXml = """
            <AllPrimitivesRecord booleanField="true"
                                 byteField="1"
                                 doubleField="6.28"
                                 enumField="ENUM_VALUE_2"
                                 floatField="3.14"
                                 instantField="2023-10-11T10:00:00Z"
                                 intField="42" longField="1000"
                                 shortField="10"
                                 stringField="example"
                                 dateField="2023-10-11T16:00:00.123Z" />""";

        // Act - Deserialization
        Document deserializationDoc = XmlUtils.ofXml(serializedXml);
        AllPrimitivesRecord deserializedRecord = subject.xmlToRecord(AllPrimitivesRecord.class, deserializationDoc);

        // Assert - Deserialized Record
        assertEquals("example", deserializedRecord.stringField());
        assertTrue(deserializedRecord.booleanField());
        assertEquals((byte) 1, deserializedRecord.byteField());
        assertEquals((short) 10, deserializedRecord.shortField());
        assertEquals(42, deserializedRecord.intField());
        assertEquals(1000L, deserializedRecord.longField());
        assertEquals(3.14f, deserializedRecord.floatField());
        assertEquals(6.28, deserializedRecord.doubleField(), 0.001);
        assertEquals(new Date(1697040000123L), deserializedRecord.dateField());
        assertEquals(Instant.parse("2023-10-11T10:00:00Z"), deserializedRecord.instantField());
        assertEquals(SomeEnum.ENUM_VALUE_2, deserializedRecord.enumField());
        assertEquals(Option.none(), deserializedRecord.optionalField());
    }
}