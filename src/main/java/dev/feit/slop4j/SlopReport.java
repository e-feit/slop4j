package dev.feit.slop4j;

import java.util.List;
import java.util.Objects;

public record SlopReport(double slopScore, SlopVerdict verdict, List<SlopFinding> findings) {

    public SlopReport {
        Objects.requireNonNull(verdict, "verdict");
        Objects.requireNonNull(findings, "findings");
        findings = List.copyOf(findings);
    }
}
