# Java Record & XML DOM Mapper
## Overview
This project provides a seamless and reusable library for **mapping between Java records** (with **Vavr collections** and **Vavr `Option`**) and **W3C DOM `Document`** objects. The main goal is to use modern Java features (such as **records**, **immutable collections**, and **sealed interfaces**) to simplify XML-based operations while enabling support for **polymorphism**, **optional fields**, and **mixed content** in XML.
The library relies on **reflection** and **conventions-over-configuration** to dynamically infer mappings between Java records and XML. By avoiding the need for annotations (e.g., JAXB), the library minimizes configuration and instead relies on well-defined conventions for mapping, ensuring a lightweight and intuitive developer experience.
## Key Features
- **Record Class Mapping**: Record class names map directly to XML element names.
- **Primitive Fields as Attributes**: Primitive fields are serialized as attributes in the XML root or child elements.
- **Polymorphism with User-Defined Sealed Interfaces**: Users define application-specific sealed interfaces, while the library provides XML-specific interfaces (`XmlText`, `XmlCData`, `XmlComment`) for handling mixed content.
- **Immutable Collections**: Supports serialization of **Vavr collections** such as `List` and `Array`.
- **Optional Fields**: Fields using `Vavr Option` are supported:
    - `Option.none()` → No corresponding XML field or attribute is serialized.
    - `Option.some(value)` → The value is serialized appropriately as an attribute or element.

- **Mixed XML Content**: Handle intermixing of **text**, **CDATA**, **comments**, and **child elements** seamlessly.
- **Type-Safety**: Use Java's records and sealed interfaces to maintain a structured and type-safe XML representation.

## Requirements & Goals
### Functional Requirements
1. **Record to Document Conversion**:
    - Dynamically map a Java record to a W3C DOM XML `Document`.
    - The record class name becomes the root element.
    - Primitive fields are serialized as attributes, and collections or complex types as nested elements.
    - Serialize polymorphic fields (e.g., `Seq` of a sealed interface) as child elements derived from their implementations.
    - Mixed content in XML (text, CDATA, comments, and elements) is supported.

2. **Document to Record Conversion**:
    - Reconstruct a Java record from an XML `Document`, respecting data types like Vavr `Option` and `Seq`.
    - Dynamically handle fields with polymorphism or mixed content.

3. **Extensible Node Content Modeling**:
    - Support distinguishing between `Text`, `CDATA`, and `Comment` nodes through library-provided interfaces.
    - Allow users to define their own sealed interface hierarchy and integrate with the library.

4. **Minimal Configuration**:
    - No annotations required, relying on conventions-over-configuration for mappings.

5. **Generic Support for Vavr Collections**:
    - Serialize Vavr collections (e.g., `Seq`) into XML-compatible formats.
    - Support polymorphic collections holding subclasses of a sealed interface.

6. **Type-Safety and Generality**:
    - Ensure operations comply with the immutability contracts of Vavr collections and Java records.
    - Dynamically discover field types using reflection for generality.

## Handling Mixed Content with Specialized Interfaces
For XML formats containing **mixed content** (text, CDATA, comments, and elements interleaved as children), the library provides specialized non-sealed interfaces to support clear distinctions. These interfaces are:
1. **XmlText**: Represents nodes containing plain text.
``` java
   public interface XmlText {
       String toXmlString();
   }
```
1. **XmlCdata**: Represents nodes containing CDATA sections.
``` java
   public interface XmlCData {
       String toXmlString();
   }
```
1. **XmlComment**: Represents nodes containing XML comments.
``` java
   public interface XmlComment {
       String toXmlString();
   }
```
## User-Defined Sealed Interfaces for XML Nodes
Users define a custom sealed interface that integrates the library-provided interfaces. For example:
``` java
public sealed interface Node permits TextNode, CDataSection, CommentNode, Child {}
```
### Implementing Text, CDATA, and Comments
Example implementations of `Node` that also implement the library interfaces:
1. **Text Node**:
To recreate TextNode mapper will use canonical constructor with String argument 
``` java
   public record TextNode(String content) implements Node, XmlText {
       @Override
       public String toXmlString() {
           return content;
       }
   }
```
1. **CDATA Section**:
To recreate CDataSection mapper will use *ofXmlString* method with String argument
In this example, the `CDataSection` stores binary data encoded as Base64 for efficient XML representation. Non-UTF-8 data or raw binary content can be safely serialized and deserialized using this approach_
``` java
   public record CDataSection(byte[] bytes) implements Node, XmlCData {
       public static CDataSection ofXmlString(String content) {
            return new CDataSection(Base64.getDecoder().decode(content));
       }
       
       @Override
       public String toXmlString() {
           return Base64.getEncoder().encodeToString(bytes);
       }
   }
```
1. **Comment Node**:
To recreate CommentNode mapper will use lambda registered by ofXmlString and toXmlString for the CommentNode class
``` java 
   public record CommentNode(Instant date) implements Node, XmlComment {
   }
   
   Mapper mapper = Mapper.stock()
      .ofXmlString(CommentNode.class, s -> new CommentNode(Instant.ofEpochMilli(Long.parseLong(s))))
      .toXmlString(CommentNode.class, c -> Long.toString(c.date().toEpochMilli()));
```
1. **Child Element**:
`Child` is modeled as a normal `ElementNode` in the `Parent` hierarchy
``` java
   public record Child(String name, Option<Integer> age) implements Node {}
```
1. **Parent Element**:
The library preserves the order of all node types (text, CDATA, comments, and elements) in mixed content, ensuring parity between the input and output XML_
``` java
   public record Parent(String organization, Seq<Node> age) {}
```
### Example Parent with Mixed Content
For XML like:
``` xml
<Parent organization="Acme">
    Text before child
    <Child name="foo" age="12" />
    <![CDATA[Some base64 encoded content]]>
    <!-- This is a comment -->
    Further text content
</Parent>
```
The equivalent Java representation:
``` java
Parent parent = new Parent("Acme", 
   List.of(
      new TextNode("Text before child"),
      new ElementNode("Child", "foo", List.empty()),
      new CDataSection(some.toByteArray()),
      new CommentNode("This is a comment"),
      new TextNode("Further text content")));
```
## Conventions for Mapping
**Note on Canonical Constructors**: All records must have constructors that match the declared fields in both order. If the canonical constructor is invalid, deserialization will fail.
### Record Class Name Mapped to Root Element
Each record maps to a corresponding XML element (root or nested). Example:
``` java
public record Person(String name, int age) {}
```
Serializes as:
``` xml
<Person name="John" age="25" />
```
### Collections with Polymorphism
Immutable collections (`Seq`) using sealed interfaces serialize as child elements. Example:
``` java
public sealed interface Vehicle permits Car, Truck {}
public record Car(String make) implements Vehicle {}
public record Truck(String model) implements Vehicle {}
public record Fleet(Seq<Vehicle> vehicles) {}
```
Serializes as:
``` xml
<Fleet>
    <Car make="Toyota" />
    <Truck model="Ford" />
</Fleet>
```
Deserialization works by inspecting the XML element name and dynamically mapping it to the appropriate subclass of the sealed interface `Vehicle`. This mapping relies on conventions based on class names.
### Collections of primitives
Immutable collections (`Seq`) of primitive type serialize as child elements, with the name of the field mapped to the element and primitive values stored as text. Example:
``` java
public record Sizes(Seq<Integer> size){}
```
Serializes as:
``` xml
<Sizes>
    <size>12</size>
    <size>13</size>
    <size>14</size>
</Sizes>
```
Deserialization works by inspecting the XML element name and dynamically mapping it to the appropriate subclass of the sealed interface `Vehicle`. This mapping relies on conventions based on class names.
### Handling Optional Fields
For Vavr `Option`:
- `Option.none()` → Field is omitted in XML.
- `Option.some(value)` → Field is serialized as an attribute or element.

### Handling primitives
Library provides mapping for the following primitive:
- String
- boolean, as true/false strings 
- numbers primitive and boxed: byte, short, int, long, float, double,
- data, instant using ISO8601 format with ms precision in UTC timezone
- enum, mapped to the enum name
The above mappings can be override by the custom primitive mapping 

### Handling custom primitives with inheritance
Implement XmlValue interface, similar to XmlText, XmlCData, XmlComment
1. **XmlValue**: Represents attribute value
``` java
   public interface XmlValue {
       String toXmlString();
   }
```
1. Implement **XmlValue** to provide mapping, static method *ofXmlString* will be used for deserialization
``` java
   public record Fraction(int numerator, int denominator) implements XmlValue {
   
       public static Fraction ofXmlString(String xml) {
            String[] parts = xml.split("/");
            return new Fraction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])); 
       }
       
       public String toXmlString() {
            return Integer.toString(numerator) + "/" + Integer.toString(denominator);
       }
   }
```
### Handling custom primitives with registration in Mapper
Note: If a custom mapping for a primitive type is provided, it will override the default library mapping for that type.
``` java
 Mapper mapper = Mapper.stock()
      .ofXmlString(Instant.class, s -> Instant.ofEpochMilli(Long.parseLong(s)))
      .toXmlString(Instant.class, i -> Long.toString(i.toEpochMilli()));
```
### Precedence of factory methods for deserialization 
This precedence ensures that custom mappings receive priority, allowing developers to handle specialized cases like validation or complex transformations, while the library falls back on conventional methods for most use cases.
1. First of all mapper search for registered factories with the mapper:
   - Mapper.ofXmlString(...) - for Primitive, XmlValue, XmlText, XmlCData, XmlComment
2. If no registered method was found, it will look for the static method with name *ofXmlString* with single String argument
3. The last resort it will look at the constructor with single string argument.

### Precedence of factory methods for serialization
This precedence ensures that custom mappings receive priority, allowing developers to handle specialized cases like validation or complex transformations, while the library falls back on conventional methods for most use cases.
1. First of all mapper search for registered factories with the mapper:
   - Mapper.toXmlString(...) - for Primitive, XmlValue, XmlText, XmlCData, XmlComment
2. If no registered method was found, it will look for the record method override with name *toXmlString* without arguments

### Using annotations
In case the compatibility with XML that is not fit into the specified conventions, extra annotations will be available:
- **@XmlName** for the record that specify tag name different from record name. Example:
 ``` java
public record IntAsText(int value) extends XmlText {}

@XmlName("int")
public record Int(String key, IntAsText value)  {
}
```
Serializes as:
``` xml
<int key="first">12</int>
```
- **@XmlName** for the field that specify attribute or tag name different from filed name. Example:
``` java
public record Value(@XmlName("string-value")String value, double percent)  {}
```
Serializes as:
``` xml
<Value string-value="first" percent="10.0"/>
```
- **@XmlNames** for sequence of sealed interface field or to the sealed interface itself, to provide alternative tag names per permitted record:
``` java
public sealed interface Vehicle permits Car, Truck {}
public record Car(String make) implements Vehicle {}
public record Truck(String model) implements Vehicle {}

public record Fleet(
            @XmlNames({
                  @XmlFor(type=Car.class, name="Fleet.Car"),
                  @XmlFor(type=Truck.class, name="Fleet.Truck")})
            Seq<Vehicle> vehicles) {}
```
Serializes as:
``` xml
<Fleet>
    <Fleet.Car make="Toyota" />
    <Fleet.Truck model="Ford" />
</Fleet>
```
- **@XmlDescription** for the record or field to provide description in Schema for the element or attribute. Example:
``` xml

@XmlDescription("Company information")
public record Company(
      @XmlDescription("Company name")
      String name, 
      
      @XmlDescription("Contains the list of vehicles belong to the company")
      Seq<Vehicle> vehicles) {}
```
## Milestones
1. **Setup and Configuration**:
    - Gradle-based setup with dependencies for **Vavr**, **JUnit**, and **W3C DOM**.

2. **Core Library Implementation**:
    - Serialize/deserialize records with support for polymorphism and mixed content.

3. **Testing**:
    - Unit tests for handling mixed content, optional fields, and polymorphic collections.

4. **Documentation**:
    - Examples and guides for library usage.

## Getting Started
### Prerequisites
- **Java 21** or newer.
- Gradle 8.x or newer.

### Build and Test
Clone the repository and run:
``` bash
./gradlew clean build
./gradlew test
```

### Usage example
`Mapper.stock()` provides a default configuration and that users can extend it with custom mappings as needed

``` java
Mapper mapper = Mapper.stock();
     // Serialize a record
     Document xml = mapper.recordToXml(new Person("John", 25));
     // Deserialize a record
     Person person = mapper.xmlToRecord(Person.class, xml);
     // Generate a Schema for the record class
     Schema schema = mapper.recordToSchema(Person.class);     
```

### Error handling
The library throws errors in specific scenarios to ensure predictable behavior during serialization and deserialization.

1. **Type Mismatch**:
   If the field type cannot be mapped to the corresponding XML attribute or child element type, an exception (`MappingException`) is thrown.

2. **Missing Required Fields**:
   Fields without default values or `Vavr Option` wrappers must have corresponding XML elements or attributes. If such a field is missing, a `MappingException` is thrown.

3. **Invalid Factories or Constructors**:
   If a registered factory method (via `Mapper.ofXmlString`) or a static/canonical constructor fails during instance creation, the exception will propagate as a `MappingException`.

4. **Unregistered Custom Primitives**:
   If a record field is not a record, not a supported primitive, does not implement a library-provided interface (`XmlValue`, `XmlText`, etc.), and lacks a registered mapping, a `MappingException` will be thrown.

5. **Unregistered Sequence Elements**:
   If record field is a **Seq** of some type that is not a record, known primitive, or sealed interface, or is Object or wildcard, it will throw exception a `MappingException`  

6. `MappingException` is a runtime exception and is not required explicit try catch. Exceptions provide detailed context about the field, type, or record that caused the failure, helping identify and resolve issues quickly.




