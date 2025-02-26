
# Java Record & XML DOM Mapper

## Overview

This project aims to provide a seamless and reusable library for **mapping between Java records** (with **Vavr collections** and **Vavr `Option`**) and **W3C DOM `Document`** objects. The main objective is to simplify XML-based operations by leveraging modern Java features like **records**, **immutable collections**, and **functional programming** (via Vavr), while also enabling robust support for **polymorphic interfaces**.

To make the mapping between Java records and XML intuitive and configurable, **JAXB annotations** will be used to define mappings. This approach ensures a declarative configuration of field mappings for XML, including support for polymorphism, optional fields, and nested collections.

The library facilitates:
- Converting Java records into W3C Document XML trees and vice versa.
- Avoiding manual mapping logic by utilizing **JAXB annotations** for configuration.
- Supporting **immutable collections**, **optional fields** (via Vavr `Option`), and polymorphism for greater flexibility.

It is designed to be **type-safe**, **generic**, and **extensible** to support various record types, providing clear abstractions and minimizing boilerplate.

---

## Requirements & Goals (Draft)

The following requirements describe the features and functionality of the library:

### Functional Requirements
1. **Record to Document Conversion**:
    - Use JAXB annotations to configure field-level mappings (e.g., customize element names, attribute bindings, etc.).
    - Dynamically generate a `Document` object from a record by respecting the JAXB mappings.
    - Serialize polymorphic interfaces so that collections of elements can handle different record implementations.

2. **Document to Record Conversion**:
    - Use JAXB annotations to reconstruct Java records from XML.
    - Enable records with nested, annotated fields or collections to map correctly to XML elements.
    - Handle **polymorphic collections** by supporting records implementing a parent interface.
    - Incorporate **optional fields** using **Vavr `Option`** without resorting to null values.

3. **Support for JAXB Customization**:
    - Allow fine-grained control of mappings using JAXB annotations like:
        - `@XmlElement`
        - `@XmlAttribute`
        - `@XmlRootElement`
        - `@XmlAccessorType`
        - `@XmlSeeAlso` (for polymorphic type resolution).
    - Flexible handling of optional fields that appear or are omitted in XML.

4. **Optional Fields with Vavr `Option`**:
    - Use `io.vavr.control.Option` to explicitly model optional fields in records.
    - Serialize `Option` fields correctly:
        - **`None`** fields should result in no corresponding XML element.
        - **`Some`** fields should serialize their value as a proper XML element or attribute.

5. **Support for Vavr Collections**:
    - Serialize **Vavr immutable collections** (e.g., `io.vavr.collection.List`, `io.vavr.collection.Map`) into an XML-compatible format during conversion.
    - Reconstruct these collections from XML when converting back to records.

6. **Type-Safety and Generality**:
    - Provide generic methods/interfaces that work with any record type.
    - Use functional programming paradigms to minimize boilerplate.

7. **Minimal Configuration**:
    - Automatically handle JAXB annotations while allowing user-defined overrides for complex mappings.

---

## JAXB Annotations

The library will rely on the following standard JAXB annotations for defining mappings:

1. **@XmlRootElement**: Maps the root of the record to a root XML element.
2. **@XmlElement**: Maps individual fields of the record to XML elements.
3. **@XmlAttribute**: Maps fields to attributes of an XML element.
4. **@XmlAccessorType**: Specifies how fields are accessed (e.g., directly or through getter methods).
5. **@XmlSeeAlso**: Supports polymorphism by specifying all record subtypes of a given interface.
6. **@XmlTransient**: Excludes specific fields from XML serialization.

---

### Example Annotations with Polymorphism and Optional Fields
```java
import javax.xml.bind.annotation.*;
import io.vavr.collection.List;
import io.vavr.control.Option;

@XmlRootElement(name = "Person")
@XmlAccessorType(XmlAccessType.FIELD)
public record Person(
    @XmlElement(name = "FullName") String name,
    @XmlAttribute(name = "personId") int id,
    @XmlElement(name = "Pets") List<Pet> pets,
    @XmlElement(name = "Address") Option<Address> address // Optional field
) {}

@XmlRootElement(name = "Pet")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Cat.class, Dog.class}) // Supporting polymorphism
public interface Pet {}

@XmlRootElement(name = "Cat")
public record Cat(@XmlElement(name = "Nickname") String name) implements Pet {}

@XmlRootElement(name = "Dog")
public record Dog(@XmlElement(name = "Breed") String breed) implements Pet {}

@XmlRootElement(name = "Address")
public record Address(
    @XmlElement(name = "City") String city,
    @XmlAttribute(name = "PostalCode") String postalCode
) {}
```

---

## Project Structure

The structure of the project is as follows:
. 
├── lib/src/main/java/io/openmapper/recordxml # Main library code 
├── lib/src/test/java/io/openmapper/recordxml # Unit tests 
├── lib/build.gradle # Gradle build configuration
├── gradle/libs.versions.toml # Gradle dependencies versions 
├── gradle.properties # Gradle properties
├── settings.gradle # Gradle settings
└── README.md # Project documentation


---

## Milestones

The project will be developed in the following stages:

1. **Setup and Initial Configuration**:
    - Set up a basic Gradle project targeting **Java 21**.
    - Include dependencies for **JAXB**, **Vavr**, **JUnit**, and **W3C DOM** utilities.

2. **Core Library Development**:
    - Implement methods to handle JAXB-based `recordToDocument` and `documentToRecord` conversions.
    - Add support for `Option` fields and handle polymorphic types in collections.

3. **Testing and Documentation**:
    - Write unit tests to cover polymorphism, optional fields, Vavr collections, and XML-to-record mappings.
    - Add usage documentation with annotated examples.

4. **Additional Features** *(Future Scope)*:
    - Support for custom serializers for complex or domain-specific field types.
    - JSON integration for mixed XML/JSON workflows.

---

## Getting Started

### Prerequisites
- **Java 21** or newer.
- Gradle 8.x or newer.

### Building the Project
Clone the repository and run:
```bash
./gradlew clean build
```

### Testing
Run all tests using:
```bash
./gradlew test
```

---

## Future Considerations (Optional Enhancements)
- Allow deeper customization of mappings including hybrid XML and JSON workflows.
- Add pluggable serializers for domain-specific requirements.
- Optimize the handling of deeply nested polymorphic structures.

---

## Related Technologies
This project combines features from several technologies:
- **JAXB**: Simplifies mapping between Java objects and XML with annotations.
- **Vavr**: Provides immutable collections and functional programming paradigms.
- **W3C DOM**: Provides the foundation for XML manipulation in Java.
- **Java Records**: Introduced in Java 16+ to represent immutable data types with minimal boilerplate.

---
