package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.ScoreMath;
import dev.feit.slop4j.internal.SlopContext;
import java.util.List;

public final class EvidenceRule implements SlopRule {

	@Override
	public Result analyze(SlopContext context, int maxFindingEvidenceLength) {
		long supportedSentences = context.sentences().stream().filter(EvidenceSignals::hasSupport).count();
		double evidenceScore = ScoreMath.ratio(supportedSentences, context.sentenceCount());
		List<SlopFinding> findings = context.tokenCount() >= 80 && evidenceScore < 0.2
				? List.of(new SlopFinding(SlopFindingType.LOW_EVIDENCE, Severity.WARNING,
						"Text contains few cited sources or measured results.", ""))
				: List.of();
		return new Result(0.0, 0.0, 0.0, 0.0, 0.0, evidenceScore, 0.0, 0.0, findings);
	}
}
