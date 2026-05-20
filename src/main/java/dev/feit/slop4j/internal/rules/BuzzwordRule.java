package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.ScoreMath;
import dev.feit.slop4j.internal.SlopContext;
import java.util.ArrayList;
import java.util.List;

public final class BuzzwordRule implements SlopRule {

    @Override
    public Result analyze(SlopContext context, int maxFindingEvidenceLength) {
        List<String> hits = new ArrayList<>();
        for (String token : context.tokens()) {
            if (context.dictionaries().buzzwords().contains(token)) {
                hits.add(token);
            }
        }

        double density = ScoreMath.ratio(hits.size(), context.tokenCount());
        List<SlopFinding> findings = new ArrayList<>();
        if (hits.size() >= 3 && density >= 0.08) {
            findings.add(
                    new SlopFinding(
                            SlopFindingType.BUZZWORD_DENSITY,
                            Severity.WARNING,
                            "Buzzword density is suspiciously high.",
                            FindingEvidence.joinDistinct(hits, maxFindingEvidenceLength)));
        }
        return new Result(density, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, findings);
    }
}
