package io.openmapper.recordxml.v5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface XmlNameSpace {
    String DEFAULT = "";
    String SCHEMA = "xs";

    String XMLNS = "xmlns";

    String value();
}
