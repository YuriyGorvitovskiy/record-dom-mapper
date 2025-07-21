package io.openmapper.recordxml.v5;

public record TypeName(String name) {
    public static TypeName of(String name) {
        return new TypeName(name);
    }

    public String simpleName() {
        return name.substring(name.indexOf(':') + 1);
    }
}
