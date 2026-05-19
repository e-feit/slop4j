package dev.feit.slop4j;

import java.util.Objects;

public record SlopFinding(
        SlopFindingType type, Severity severity, String message, String evidence) {

    public SlopFinding {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(evidence, "evidence");
    }
}
