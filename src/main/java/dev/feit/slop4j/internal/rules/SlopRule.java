package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.internal.SlopContext;
import java.util.List;

public interface SlopRule {

    Result analyze(SlopContext context, int maxFindingEvidenceLength);

    public record Result(
            double buzzwordDensity,
            double vaguePhraseDensity,
            double abstractNounDensity,
            double concretenessScore,
            double actionabilityScore,
            double evidenceScore,
            double repetitionScore,
            double overconfidenceScore,
            List<SlopFinding> findings) {

        public Result {
            findings = List.copyOf(findings);
        }

        public static Result empty() {
            return new Result(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, List.of());
        }
    }
}
