package io.openmapper.recordxml;

public record Attribute(String name, String value) implements Unit {
    public static  Attribute of(String name, String value) {
        return new Attribute(name, value);
    }
}
