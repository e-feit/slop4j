package dev.feit.slop4j;

import java.util.List;
import java.util.Objects;

/**
 * Result of a deterministic slop analysis.
 *
 * <p>The final {@code slopScore} is normalized to {@code 0.0..100.0}. All other numeric scores are
 * normalized to {@code 0.0..1.0}.
 */
public record SlopReport(
        double slopScore,
        double buzzwordDensity,
        double vaguePhraseDensity,
        double concretenessScore,
        double actionabilityScore,
        double evidenceScore,
        double repetitionScore,
        double overconfidenceScore,
        SlopVerdict verdict,
        List<SlopFinding> findings) {

    public SlopReport {
        Objects.requireNonNull(verdict, "verdict");
        Objects.requireNonNull(findings, "findings");
        slopScore = clamp(slopScore, 0.0, 100.0);
        buzzwordDensity = clamp01(buzzwordDensity);
        vaguePhraseDensity = clamp01(vaguePhraseDensity);
        concretenessScore = clamp01(concretenessScore);
        actionabilityScore = clamp01(actionabilityScore);
        evidenceScore = clamp01(evidenceScore);
        repetitionScore = clamp01(repetitionScore);
        overconfidenceScore = clamp01(overconfidenceScore);
        findings = List.copyOf(findings);
    }

    private static double clamp01(double value) {
        return clamp(value, 0.0, 1.0);
    }

    private static double clamp(double value, double minimum, double maximum) {
        if (Double.isNaN(value)) {
            return minimum;
        }
        return Math.max(minimum, Math.min(maximum, value));
    }
}
