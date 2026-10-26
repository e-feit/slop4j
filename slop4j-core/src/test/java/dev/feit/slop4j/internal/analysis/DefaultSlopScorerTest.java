package dev.feit.slop4j.internal.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import dev.feit.slop4j.internal.rules.SlopRule;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefaultSlopScorerTest {

	@Test
	void unrelatedEvidenceDoesNotReduceUnsupportedClaimPenalty() {
		SlopContext context = SlopContext.create("", new ResourceDictionaryLoader().load(Language.ENGLISH));
		SlopRule.Result unsupportedClaim = new SlopRule.Result(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, List.of());
		SlopRule.Result unrelatedEvidence = new SlopRule.Result(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, List.of());
		DefaultSlopScorer scorer = new DefaultSlopScorer();

		SlopReport withoutEvidence = scorer.score(context, List.of(unsupportedClaim));
		SlopReport withEvidence = scorer.score(context, List.of(unsupportedClaim, unrelatedEvidence));

		assertThat(withEvidence.slopScore()).isEqualTo(withoutEvidence.slopScore());
	}

	@Test
	void unrelatedEvidenceDoesNotChangeUnsupportedClaimVerdict() {
		SlopContext context = SlopContext.create("Always guaranteed. The run measured 95% of requests.",
				new ResourceDictionaryLoader().load(Language.ENGLISH));
		SlopRule.Result unsupportedClaim = new SlopRule.Result(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, List.of());
		SlopRule.Result unrelatedEvidence = new SlopRule.Result(0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, List.of());

		SlopReport report = new DefaultSlopScorer().score(context, List.of(unsupportedClaim, unrelatedEvidence));

		assertThat(report.verdict()).isEqualTo(SlopVerdict.BRAIN_FREE_ZONE);
	}
}
