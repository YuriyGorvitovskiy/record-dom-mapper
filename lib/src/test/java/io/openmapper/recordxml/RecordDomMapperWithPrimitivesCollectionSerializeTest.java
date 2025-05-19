package io.openmapper.recordxml;

import io.vavr.collection.Seq;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordDomMapperWithPrimitivesCollectionSerializeTest {

    record Sizes(Seq<Integer> size) {}

    Mapper subject = Mapper.stock();

    @Test
    void recordToXml_collectionOfPrimitives() throws Exception {
        // Arrange
        Sizes sizes = new Sizes(io.vavr.collection.List.of(12, 15, 20));

        // Act
        Document document = subject.recordToXml(XmlUtils.builder(), sizes);
        String result = XmlUtils.toXml(document); // Convert Document to XML string for assertions

        // Assert
        String expectedXml = """
                <Sizes>
                  <size>12</size>
                  <size>15</size>
                  <size>20</size>
                </Sizes>""";
        assertEquals(expectedXml.strip(), result.strip(), "Serialized XML does not match!");
    }

    @Test
    void xmlToRecord_collectionOfPrimitives() throws Exception {
        // Arrange
        String sourceXml = """
                <Sizes>
                  <size>12</size>
                  <size>15</size>
                  <size>20</size>
                </Sizes>""";

        Document document = XmlUtils.ofXml(sourceXml); // Utility method to parse XML string into Document

        // Act
        Sizes sizes = subject.xmlToRecord(Sizes.class, document);

        // Assert
        assertEquals(3, sizes.size().size(), "The size of the collection is incorrect!");
        assertEquals(12, sizes.size().get(0).intValue());
        assertEquals(15, sizes.size().get(1).intValue());
        assertEquals(20, sizes.size().get(2).intValue());
    }
}