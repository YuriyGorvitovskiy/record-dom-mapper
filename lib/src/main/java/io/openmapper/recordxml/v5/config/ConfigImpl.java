package io.openmapper.recordxml.v5.config;

import java.lang.reflect.*;

import io.openmapper.recordxml.util.Java;
import io.openmapper.recordxml.util.SoftenEx;
import io.openmapper.recordxml.v5.Config;
import io.openmapper.recordxml.v5.Mapper;
import io.openmapper.recordxml.v5.MappingResolver;
import io.openmapper.recordxml.v5.MappingType;
import io.openmapper.recordxml.xsd.*;
import io.openmapper.recordxml.xsd.XsdSimple.Predefined;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public record ConfigImpl(Function1<Type, Mapper> memoizedMappers) implements Config {

    record FieldMapping(String name, MappingType mapping, Method extract) {

    }

    public static final ConfigImpl DEFAULT = new ConfigImpl(null);

    public ConfigImpl(Function1<Type, Mapper> memoizedMappers) {
        this.memoizedMappers = Function1.of(this::calculateMapper).memoized();
    }

    Mapper calculateMapper(Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        if (rawClass == String.class) {
            return new StringMapper();
        }
        if (Seq.class.isAssignableFrom(rawClass)) {
            return SeqMapper.of(this, declaredType);
        }
        if (Map.class.isAssignableFrom(rawClass)) {
            return MapMapper.of(this, declaredType);
        }
        if (rawClass.isInterface() && rawClass.isSealed()) {
            return InterfaceMapper.of(this, declaredType);
        }
        if (rawClass.isRecord()) {
            return RecordMapper.of(this, declaredType);
        }
        throw new IllegalArgumentException("Unsupported type: " + declaredType);
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
            public XsdComplex xsdType(MappingResolver resolver) {
                Seq<ConfigImpl.FieldMapping> mappings = fields.map((c) -> new ConfigImpl.FieldMapping(c.getName(), resolver.resolveType(c.getGenericType()), c.getAccessor()));
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
            public Seq<XsdType> xsdDeclaredTypes(MappingResolver resolver) {
                return Array.of(xsdType(resolver));
            }

        };
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

    public Mapper mapperFor(Type declaredType) {
        return memoizedMappers.apply(declaredType);
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
            public XsdType xsdType(MappingResolver resolver) {
                return Predefined.STRING;
            }

            @Override
            public Seq<XsdType> xsdDeclaredTypes(MappingResolver resolver) {
                return Array.of(xsdType(resolver));
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
            public XsdType xsdType(MappingResolver resolver) {
                return xsdDeclaredTypes(resolver).get();
            }

            @Override
            public Seq<XsdType> xsdDeclaredTypes(MappingResolver resolver) {
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

    private MappingType buildInterfaceMapping(Type declaredType) {
        Class<?> rawClass = Java.rawClass(declaredType);
        XsdTypeRef ref = XsdTypeRef.of(rawClass.getSimpleName());

        Seq<Class<?>> permitted = Java.collectPermittedRecords(rawClass);
        Map<String, Class<?>> typesByName = permitted.toMap(Class::getSimpleName, c -> c);
        Map<Class<?>, String> namesByType = typesByName.toMap(Tuple2::swap);

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
            public XsdType xsdType(MappingResolver resolver) {
                return XsdComplex.of(ref)
                        .addElements(permitted.map(p -> XsdElement.of(
                                p.getSimpleName(),
                                resolver.resolveType(p).xsdRef())));
            }

            @Override
            public Seq<XsdType> xsdDeclaredTypes(MappingResolver resolver) {
                return Array.of(xsdType(resolver));
            }
        };
    }
}
