package io.openmapper.recordxml;

import io.openmapper.recordxml.v2.Mapper;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordDomMapperRecordWithPrimitivesTest {

    public record Person(String name, int age) {
    }

    Mapper subject = Mapper.stock();

    @Test
    void recordToXml() throws Exception {
        // Arrange
        Person person = new Person("John", 25);

        // Act
        Document document = subject.recordToXml(XmlUtils.builder(), person);
        String result = XmlUtils.toXml(document); // Convert Document to String for assertions

        // Assert
        String expectedXml = """
                <Person age="25" name="John"/>""";
        assertEquals(expectedXml, result.strip(), "Serialized XML does not match!");
    }

    @Test
    void xmlToRecord() throws Exception {
        // Arrange
        String sourceXml = """
                <Person age="30" name="Jane"/>""";

        Document document = XmlUtils.ofXml(sourceXml); // Utility method to parse XML string into Document

        // Act
        Person person = subject.xmlToRecord(Person.class, document);

        // Assert
        assertEquals("Jane", person.name(), "Name is incorrect!");
        assertEquals(30, person.age(), "Age is incorrect!");
    }
}
