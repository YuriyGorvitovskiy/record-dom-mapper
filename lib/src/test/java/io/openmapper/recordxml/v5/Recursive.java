package io.openmapper.recordxml.v5;

public record Recursive(String name,
                        Recursive recursive) {
}
