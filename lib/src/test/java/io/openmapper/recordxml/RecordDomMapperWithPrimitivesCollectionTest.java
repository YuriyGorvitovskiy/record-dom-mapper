package io.openmapper.recordxml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordDomMapperWithPrimitivesCollectionTest {

    record Sizes(io.vavr.collection.Seq<Integer> size) {}

    Mapper subject = Mapper.stock();

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