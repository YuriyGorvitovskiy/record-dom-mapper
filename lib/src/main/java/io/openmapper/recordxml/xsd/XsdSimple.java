package io.openmapper.recordxml.xsd;

public non-sealed interface XsdSimple extends XsdType {

    enum Predefined implements XsdSimple {
        BOOLEAN("xs:boolean"),
        STRING("xs:string"),
        NORMALIZED_STRING("xs:normalizedString"),
        BYTE("xs:byte"),
        SHORT("xs:short"),
        LONG("xs:long"),
        INTEGER("xs:integer"),
        FLOAT("xs:float"),
        DOUBLE("xs:double"),
        DATE_TIME("xs:dateTime"),
        DATE("xs:date"),
        DURATION("xs:duration"),
        TIME("xs:time"),
        HEX_BINARY("xs:hexBinary"),
        BASE64_BINARY("xs:base64Binary"),
        QNAME("xs:QName"),
        NAME("xs:Name"),
        NCNAME("xs:NCName"),
        ID("xs:ID"),
        IDREF("xs:IDREF"),
        ANY_URI("xs:anyURI");

        private final XsdTypeRef ref;

        Predefined(String xsdName) {
            this.ref = XsdTypeRef.of(xsdName);
        }

        @Override
        public XsdTypeRef ref() {
            return ref;
        }
    }
}
