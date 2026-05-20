package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.ScoreMath;
import dev.feit.slop4j.internal.SlopContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RepetitionRule implements SlopRule {

    @Override
    public Result analyze(SlopContext context, int maxFindingEvidenceLength) {
        if (context.tokenCount() < 30) {
            return Result.empty();
        }

        Map<String, Integer> ngrams = new HashMap<>();
        for (int index = 0; index <= context.tokens().size() - 3; index++) {
            String ngram = String.join(" ", context.tokens().subList(index, index + 3));
            ngrams.merge(ngram, 1, Integer::sum);
        }

        int repeatedNgramCount = 0;
        List<String> repeated = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ngrams.entrySet()) {
            if (entry.getValue() > 1) {
                repeatedNgramCount += entry.getValue() - 1;
                repeated.add(entry.getKey());
            }
        }

        double repetitionScore = ScoreMath.ratio(repeatedNgramCount, context.tokenCount());
        List<SlopFinding> findings = new ArrayList<>();
        if (repetitionScore >= 0.08) {
            findings.add(
                    new SlopFinding(
                            SlopFindingType.REPETITION,
                            Severity.WARNING,
                            "Repeated phrasing may indicate templated or low-information text.",
                            FindingEvidence.joinDistinct(repeated, maxFindingEvidenceLength)));
        }

        return new Result(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, repetitionScore, 0.0, findings);
    }
}
