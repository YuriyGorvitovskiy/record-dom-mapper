package io.openmapper.recordxml;

import io.openmapper.recordxml.v2.Mapper;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordDomMapperWithNestedAndCollectionRecordsTest {

    // Example record with nested records and a Seq of records
    public record Address(String street, String city, String zipCode) {
    }

    public record Person(String name, int age, Address address, Seq<Address> previousAddresses) {
    }

    Mapper subject = Mapper.stock();

    /**
     * Serialize a record with nested fields and a Seq of records
     */
    @Test
    void recordToXml_nestedAndCollectionRecords() throws Exception {
        // Arrange
        Address currentAddress = new Address("123 Main St", "Springfield", "12345");
        Seq<Address> previousAddresses = List.of(
                new Address("456 Elm St", "Shelbyville", "67890"),
                new Address("789 Oak St", "Capital City", "13579")
        );
        Person person = new Person("John Doe", 30, currentAddress, previousAddresses);

        // Act
        Document document = subject.recordToXml(XmlUtils.builder(), person);
        String result = XmlUtils.toXml(document);

        // Assert
        String expectedXml = """
                <Person age="30" name="John Doe">
                    <address city="Springfield" street="123 Main St" zipCode="12345"/>
                    <previousAddresses city="Shelbyville" street="456 Elm St" zipCode="67890"/>
                    <previousAddresses city="Capital City" street="789 Oak St" zipCode="13579"/>
                </Person>""";
        assertEquals(expectedXml.strip(), result.strip(), "Serialized XML does not match!");
    }

    /**
     * Deserialize XML into a record with nested fields and a Seq of records
     */
    @Test
    void xmlToRecord_nestedAndCollectionRecords() throws Exception {
        // Arrange
        String sourceXml = """
                <Person age="30" name="John Doe">
                  <address street="123 Main St" city="Springfield" zipCode="12345"/>
                  <previousAddresses street="456 Elm St" city="Shelbyville" zipCode="67890"/>
                  <previousAddresses street="789 Oak St" city="Capital City" zipCode="13579"/>
                </Person>""";

        Document document = XmlUtils.ofXml(sourceXml);

        // Act
        Person person = subject.xmlToRecord(Person.class, document);

        // Assert
        // Check main fields
        assertEquals("John Doe", person.name());
        assertEquals(30, person.age());

        // Check nested Address field
        Address currentAddress = person.address();
        assertEquals("123 Main St", currentAddress.street());
        assertEquals("Springfield", currentAddress.city());
        assertEquals("12345", currentAddress.zipCode());

        // Check Seq of previous addresses
        Seq<Address> previousAddresses = person.previousAddresses();
        assertEquals(2, previousAddresses.size(), "Number of previous addresses is incorrect!");

        Address firstPrevious = previousAddresses.get(0);
        assertEquals("456 Elm St", firstPrevious.street());
        assertEquals("Shelbyville", firstPrevious.city());
        assertEquals("67890", firstPrevious.zipCode());

        Address secondPrevious = previousAddresses.get(1);
        assertEquals("789 Oak St", secondPrevious.street());
        assertEquals("Capital City", secondPrevious.city());
        assertEquals("13579", secondPrevious.zipCode());
    }
}