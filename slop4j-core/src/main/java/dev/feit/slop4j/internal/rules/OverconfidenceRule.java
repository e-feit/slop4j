package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.ScoreMath;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.TextTokenizer;
import java.util.ArrayList;
import java.util.List;

public final class OverconfidenceRule implements SlopRule {

	@Override
	public Result analyze(SlopContext context, int maxFindingEvidenceLength) {
		List<String> hits = new ArrayList<>();
		for (String sentence : context.sentences()) {
			if (!EvidenceSignals.hasSupport(sentence)) {
				for (String token : TextTokenizer.tokens(sentence)) {
					if (context.dictionaries().claimMarkers().contains(token)) {
						hits.add(token);
					}
				}
			}
		}

		double score = ScoreMath.ratio(hits.size(), context.sentenceCount());
		List<SlopFinding> findings = new ArrayList<>();
		if (hits.size() >= 2) {
			findings.add(new SlopFinding(SlopFindingType.OVERCONFIDENCE, Severity.WARNING,
					"Text makes highly confident claims without matching evidence.",
					FindingEvidence.joinDistinct(hits, maxFindingEvidenceLength)));
		}

		return new Result(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, score, findings);
	}
}
