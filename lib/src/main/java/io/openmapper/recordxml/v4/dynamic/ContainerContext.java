package io.openmapper.recordxml.v4.dynamic;

import io.openmapper.recordxml.xml.XmlUnit;
import io.vavr.collection.Seq;

public interface ContainerContext {
    Object ofElements(Seq<XmlUnit> objects);
}
