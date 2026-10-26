package dev.feit.slop4j.internal.rules;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import org.junit.jupiter.api.Test;

class OverconfidenceRuleTest {

	@Test
	void detectsClaimMarkersWithoutEvidence() {
		SlopRule.Result result = new OverconfidenceRule()
				.analyze(SlopContext.create("This is always guaranteed and obviously proven.",
						new ResourceDictionaryLoader().load(Language.ENGLISH)), 120);

		assertThat(result.overconfidenceScore()).isGreaterThan(0.0);
		assertThat(result.findings()).extracting(finding -> finding.type()).contains(SlopFindingType.OVERCONFIDENCE);
	}

	@Test
	void evidenceInAnotherSentenceDoesNotSupportAnAbsoluteClaim() {
		SlopRule.Result result = analyze(
				"This always succeeds and is guaranteed. The unrelated run processed 95% of requests.");

		assertThat(result.overconfidenceScore()).isGreaterThan(0.0);
		assertThat(result.findings()).extracting(finding -> finding.type()).contains(SlopFindingType.OVERCONFIDENCE);
	}

	@Test
	void measuredResultInSameSentenceSupportsClaim() {
		SlopRule.Result result = analyze(
				"This always succeeds and is guaranteed: 100% success was observed in 200 tests.");

		assertThat(result.overconfidenceScore()).isZero();
		assertThat(result.findings()).isEmpty();
	}

	@Test
	void numericalPromiseDoesNotCountAsMeasuredResult() {
		SlopRule.Result result = analyze("This always works for 100 users and guarantees 100% availability.");

		assertThat(result.overconfidenceScore()).isGreaterThan(0.0);
	}

	@Test
	void measurementInNextListItemDoesNotSupportClaim() {
		SlopRule.Result result = analyze("- Always guaranteed\n- We observed 95% of requests passed");

		assertThat(result.overconfidenceScore()).isGreaterThan(0.0);
	}

	private static SlopRule.Result analyze(String text) {
		return new OverconfidenceRule()
				.analyze(SlopContext.create(text, new ResourceDictionaryLoader().load(Language.ENGLISH)), 120);
	}
}
