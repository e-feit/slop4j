package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.ScoreMath;
import dev.feit.slop4j.internal.SlopContext;
import java.util.ArrayList;
import java.util.List;

public final class VaguePhraseRule implements SlopRule {

	@Override
	public Result analyze(SlopContext context, int maxFindingEvidenceLength) {
		List<String> hits = new ArrayList<>();
		for (String phrase : context.dictionaries().vaguePhrases()) {
			if (context.normalizedText().contains(phrase)) {
				hits.add(phrase);
			}
		}

		double density = ScoreMath.ratio(hits.size(), context.sentenceCount());
		List<SlopFinding> findings = new ArrayList<>();
		if (hits.size() >= 2) {
			findings.add(new SlopFinding(SlopFindingType.VAGUE_PHRASE, Severity.WARNING,
					"Text uses vague phrases that reduce operational specificity.",
					FindingEvidence.joinDistinct(hits, maxFindingEvidenceLength)));
		}
		return new Result(0.0, density, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, findings);
	}
}
