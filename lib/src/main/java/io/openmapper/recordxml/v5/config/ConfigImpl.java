package io.openmapper.recordxml.v5.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Objects;

import io.openmapper.recordxml.util.Java;
import io.openmapper.recordxml.util.SoftenEx;
import io.openmapper.recordxml.v5.Config;
import io.openmapper.recordxml.v5.MappingType;
import io.openmapper.recordxml.v5.XsdResolver;
import io.openmapper.recordxml.xml.XmlAttribute;
import io.openmapper.recordxml.xml.XmlElement;
import io.openmapper.recordxml.xsd.*;
import io.openmapper.recordxml.xsd.XsdSimple.Predefined;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public record ConfigImpl() implements Config {

    record FieldMapping(String name, MappingType mapping) {

    }

    @Override
    public MappingType select(Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        if (rawClass == String.class) {
            return buildStringMapping();
        }
        if (Map.class.isAssignableFrom(rawClass)) {
            return buildMapMapping(declaredType);
        }
        if (rawClass.isInterface() && rawClass.isSealed()) {
            return buildInterfaceMapping(declaredType);
        }
        if (rawClass.isRecord()) {
            return buildRecordMapping(declaredType);
        }
        throw new IllegalArgumentException("Unsupported type: " + declaredType);
    }

    private MappingType buildStringMapping() {
        return new MappingType() {
            @Override
            public boolean isSimple() {
                return true;
            }

            @Override
            public boolean isPolymorphic() {
                return false;
            }

            @Override
            public XsdTypeRef xsdRef() {
                return Predefined.STRING.ref();
            }

            @Override
            public XsdType xsdType(XsdResolver resolver) {
                return Predefined.STRING;
            }

            @Override
            public Seq<XsdType> xsdDeclaredTypes(XsdResolver resolver) {
                return Array.of(xsdType(resolver));
            }

            @Override
            public Object ofXml(String text) {
                return text;
            }

            @Override
            public Object ofXml(XsdResolver resolver, XmlElement parent, Seq<XmlElement> elements) {
                throw new IllegalArgumentException("Should never be called for String Mapping");
            }
        };
    }

    private MappingType buildMapMapping(Type declaredType) {
        Type keyType = ((ParameterizedType) declaredType).getActualTypeArguments()[0];
        Type valueType = ((ParameterizedType) declaredType).getActualTypeArguments()[1];

        MappingType keyMapping = select(keyType);
        if (!keyMapping.isSimple()) {
            throw new IllegalArgumentException("Key type must be simple: " + keyType);
        }
        MappingType valueMapping = select(valueType);
        String keyTypeName = keyMapping.xsdRef().nameWithoutNamespace();
        XsdTypeRef ref = XsdTypeRef.of(valueMapping.xsdRef().nameWithoutNamespace() + "_MappedBy_" + keyTypeName);

        return new MappingType() {
            @Override
            public boolean isSimple() {
                return false;
            }

            @Override
            public boolean isPolymorphic() {
                return valueMapping.isPolymorphic();
            }

            @Override
            public XsdTypeRef xsdRef() {
                return ref;
            }

            @Override
            public XsdType xsdType(XsdResolver resolver) {
                return xsdDeclaredTypes(resolver).get();
            }

            @Override
            public Seq<XsdType> xsdDeclaredTypes(XsdResolver resolver) {
                XsdComplex main = XsdComplex.of(ref);
                if (valueMapping.isSimple() || !valueMapping.isPolymorphic()) {
                    return Array.of(extensionWithKey(main, valueMapping.xsdRef()));
                }
                XsdComplex base = (XsdComplex) valueMapping.xsdType(resolver);
                var mappedTypes = base.elements().map(e -> extensionWithKey(e.type()));
                var mappedElements = base.elements().map(e -> e.withType(mappedByTypeRef(e.type())));

                main = main.addElements(mappedElements);
                return mappedTypes.prepend(main);
            }

            @Override
            public Object ofXml(String text) {
                throw new IllegalArgumentException("Should never be called for Map Mapping");
            }

            @Override
            public Object ofXml(XsdResolver resolver, XmlElement parent, Seq<XmlElement> elements) {
                if (valueMapping.isSimple()) {
                    return elements.toMap(
                            e -> keyMapping.ofXml(e.attributes().get("Key").get().value()),
                            e -> valueMapping.ofXml(e.text()));
                }
                return elements.toMap(
                        e -> keyMapping.ofXml(e.attributes().get("Key").get().value()),
                        e -> valueMapping.ofXml(resolver, parent, Array.of(e)));
            }

            XsdComplex extensionWithKey(XsdComplex complex, XsdTypeRef base) {
                return complex
                        .extensionOf(base)
                        .addAttributes(XsdAttribute.of("Key", keyMapping.xsdRef()));
            }

            XsdType extensionWithKey(XsdTypeRef base) {
                XsdTypeRef ref = mappedByTypeRef(base);
                return extensionWithKey(XsdComplex.of(ref), base);
            }

            XsdTypeRef mappedByTypeRef(XsdTypeRef base) {
                return XsdTypeRef.of(base.nameWithoutNamespace() + "_MappedBy_" + keyTypeName);
            }

        };
    }


    private MappingType buildRecordMapping(Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        Seq<RecordComponent> fields = Array.of(rawClass.getRecordComponents());
        Constructor<?> constructor = SoftenEx.call(() -> rawClass.getDeclaredConstructor(fields.map(RecordComponent::getType).toJavaArray(Class[]::new)));

        XsdTypeRef ref = XsdTypeRef.of(rawClass.getSimpleName());

        return new MappingType() {
            @Override
            public boolean isSimple() {
                return false;
            }

            @Override
            public boolean isPolymorphic() {
                return false;
            }

            @Override
            public XsdTypeRef xsdRef() {
                return ref;
            }

            @Override
            public XsdComplex xsdType(XsdResolver resolver) {
                Seq<ConfigImpl.FieldMapping> mappings = fields.map((c) -> new ConfigImpl.FieldMapping(c.getName(), resolver.resolveType(c.getGenericType())));
                var simpleAndComplex = mappings.partition(m -> m.mapping.isSimple());
                boolean requiredFieldElement = (simpleAndComplex._2.size() > 1);
                XsdComplex result = XsdComplex.of(ref)
                        .addAttributes(simpleAndComplex._1.map(m -> XsdAttribute.of(m.name, m.mapping().xsdRef())));

                if (requiredFieldElement) {
                    result = result.addElements(simpleAndComplex._2.map(m -> XsdElement.of(m.name, m.mapping().xsdRef())));
                } else if (simpleAndComplex._2.size() == 1) {
                    FieldMapping fieldMapping = simpleAndComplex._2.get();
                    if (fieldMapping.mapping.isPolymorphic()) {
                        result = result.mergeType((XsdComplex) fieldMapping.mapping.xsdType(resolver));
                    } else {
                        result = result.addElements(XsdElement.of(fieldMapping.name, fieldMapping.mapping().xsdRef()));
                    }
                }

                return result;
            }

            @Override
            public Seq<XsdType> xsdDeclaredTypes(XsdResolver resolver) {
                return Array.of(xsdType(resolver));
            }

            @Override
            public Object ofXml(String text) {
                throw new IllegalArgumentException("Should never be called for Map Mapping");
            }

            @Override
            public Object ofXml(XsdResolver resolver, XmlElement parent, Seq<XmlElement> elements) {
                if (elements.isEmpty()) {
                    return null;
                }

                XmlElement self = elements.head();

                Seq<ConfigImpl.FieldMapping> mappings = fields.map((c) -> new ConfigImpl.FieldMapping(c.getName(), resolver.resolveType(c.getGenericType())));
                boolean requiredFieldElement = mappings.count(m -> !m.mapping.isSimple()) > 1;

                Object[] fields = mappings.map(m -> {
                    if (m.mapping.isSimple()) {
                        Option<XmlAttribute> attr = self.attributes().get(m.name);
                        return attr.map(a -> m.mapping.ofXml(a.value())).getOrNull();
                    }
                    if (!m.mapping.isPolymorphic()) {
                        Seq<XmlElement> children = self.elements().filter(e -> Objects.equals(e.name(), m.name()));
                        return m.mapping.ofXml(resolver, self, children);
                    }
                    if (requiredFieldElement) {
                        Option<XmlElement> root = self.elements().find(e -> Objects.equals(e.name(), m.name()));
                        return root.map(r -> m.mapping.ofXml(resolver, r, r.elements())).getOrNull();
                    }
                    return m.mapping.ofXml(resolver, self, self.elements());

                }).toJavaArray();
                return SoftenEx.call(() -> constructor.newInstance(fields));
            }
        };
    }

    private MappingType buildInterfaceMapping(Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        XsdTypeRef ref = XsdTypeRef.of(rawClass.getSimpleName());

        Seq<Class<?>> permitted = Java.collectPermittedRecords(rawClass);
        Map<String, Class<?>> typesByName = permitted.toMap(Class::getSimpleName, c -> c);

        return new MappingType() {
            @Override
            public boolean isSimple() {
                return false;
            }

            @Override
            public boolean isPolymorphic() {
                return true;
            }

            @Override
            public XsdTypeRef xsdRef() {
                return ref;
            }

            @Override
            public XsdType xsdType(XsdResolver resolver) {
                return XsdComplex.of(ref)
                        .addElements(permitted.map(p -> XsdElement.of(
                                p.getSimpleName(),
                                resolver.resolveType(p).xsdRef())));
            }

            @Override
            public Seq<XsdType> xsdDeclaredTypes(XsdResolver resolver) {
                return Array.of(xsdType(resolver));
            }

            @Override
            public Object ofXml(String text) {
                throw new IllegalArgumentException("Should never be called for Map Mapping");
            }

            @Override
            public Object ofXml(XsdResolver resolver, XmlElement parent, Seq<XmlElement> elements) {
                if (elements.isEmpty()) {
                    return null;
                }
                XmlElement self = elements.head();
                Class<?> clazz = typesByName.get(self.name()).get();
                MappingType mapping = resolver.resolveType(clazz);
                if (mapping.isSimple()) {
                    return mapping.ofXml(self.text());
                }
                return mapping.ofXml(resolver, parent, elements);
            }
        };
    }
}
