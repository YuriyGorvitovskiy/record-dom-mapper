package io.openmapper.recordxml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;

class RecordToDomMapperTest {

    final RecordToDomMapper subject = RecordToDomMapper.stock(); // Test subject variable!

    @Test
    void mapsSimpleRecordToDom() {
        // Setup
        record SimpleRecord(String name, int age) {
        }
        SimpleRecord record = new SimpleRecord("John", 28);

        // Execution
        Document doc = subject.mapToDom(record);

        // Validate
        Element rootElement = doc.getDocumentElement();
        assertEquals("SimpleRecord", rootElement.getTagName());
        assertEquals("John", rootElement.getElementsByTagName("name").item(0).getTextContent());
        assertEquals("28", rootElement.getElementsByTagName("age").item(0).getTextContent());
    }

    @Test
    void mapsRecordWithNullValuesCorrectly() {
        // Setup
        record NullableRecord(String name, String address) {
        }
        NullableRecord record = new NullableRecord("John", null);

        // Execution
        Document doc = subject.mapToDom(record);

        // Validate
        Element rootElement = doc.getDocumentElement();
        assertEquals("NullableRecord", rootElement.getTagName());
        assertEquals("John", rootElement.getElementsByTagName("name").item(0).getTextContent());
        assertEquals(0, rootElement.getElementsByTagName("address").getLength()); // Null generates empty tag
    }

    @Test
    void mapsNestedRecordsToDom()  {
        // Setup
        record Address(String city, String state) {
        }
        record Person(String name, Address address) {
        }
        Address address = new Address("Seattle", "WA");
        Person record = new Person("Alice", address);

        // Execution
        Document doc = subject.mapToDom(record);

        // Validate
        Element rootElement = doc.getDocumentElement();
        assertEquals("Person", rootElement.getTagName());

        Element addressElement = (Element) rootElement.getElementsByTagName("address").item(0);
        assertNotNull(addressElement);
        assertEquals("Seattle", addressElement.getElementsByTagName("city").item(0).getTextContent());
        assertEquals("WA", addressElement.getElementsByTagName("state").item(0).getTextContent());
    }

    @Test
    void mapsEmptyRecordToDom() {
        // Setup
        record EmptyRecord() {
        }
        EmptyRecord record = new EmptyRecord();

        // Execution
        Document doc = subject.mapToDom(record);

        // Validate
        Element rootElement = doc.getDocumentElement();
        assertEquals("EmptyRecord", rootElement.getTagName());
        assertEquals(0, rootElement.getChildNodes().getLength()); // Empty record = no fields
    }

    @Test
    void throwsExceptionForUnsupportedFieldTypes() {
        // Setup
        record UnsupportedRecord(Object unsupportedField) {
        }
        UnsupportedRecord record = new UnsupportedRecord(new Object());

        // Execution
        // Validate
        assertThrows(RuntimeException.class, () -> subject.mapToDom(record));
    }


}