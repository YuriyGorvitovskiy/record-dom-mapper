package io.openmapper.recordxml;

import io.openmapper.recordxml.v2.Mapper;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecordDomMapperWithOptionalFieldsTest {

    public record Employee(String id, Option<String> department) {
    }

    Mapper subject = Mapper.stock();

    @Test
    void recordToXml_withDepartment() throws Exception {
        // Arrange
        Employee employee = new Employee("123", Option.some("HR"));

        // Act
        Document document = subject.recordToXml(XmlUtils.builder(), employee);
        String result = XmlUtils.toXml(document); // Convert Document to String for assertions

        // Assert
        String expectedXml = """
                <Employee department="HR" id="123"/>""";
        assertEquals(expectedXml, result.strip(), "Serialized XML does not match!");
    }

    @Test
    void recordToXml_withoutDepartment() throws Exception {
        // Arrange
        Employee employee = new Employee("123", Option.none());

        // Act
        Document document = subject.recordToXml(XmlUtils.builder(), employee);
        String result = XmlUtils.toXml(document); // Convert Document to String for assertions

        // Assert
        String expectedXml = """
                <Employee id="123"/>""";

        assertEquals(expectedXml, result.strip(), "Serialized XML does not match!");
    }


    @Test
    void xmlToRecord_withDepartment() throws Exception {
        // Arrange
        String sourceXml = """
                <Employee id="123" department="HR" />""";

        Document document = XmlUtils.ofXml(sourceXml);

        // Act
        Employee employee = subject.xmlToRecord(Employee.class, document);

        // Assert
        assertEquals("123", employee.id(), "ID is incorrect!");
        assertTrue(employee.department().isDefined(), "Department should be defined!");
        assertEquals("HR", employee.department().get(), "Department is incorrect!");
    }

    @Test
    void xmlToRecord_withoutDepartment() throws Exception {
        // Arrange
        String sourceXml = """
                <Employee id="123" />""";

        Document document = XmlUtils.ofXml(sourceXml); // Utility method to parse XML string into Document

        // Act
        Employee employee = subject.xmlToRecord(Employee.class, document);

        // Assert
        assertEquals("123", employee.id(), "ID is incorrect!");
        assertTrue(employee.department().isEmpty(), "Department should not be defined!");
    }

}
