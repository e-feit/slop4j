package dev.feit.slop4j.internal.rules;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import org.junit.jupiter.api.Test;

class EvidenceRuleTest {

	@Test
	void scoresSupportingSignalsPerSentence() {
		SlopRule.Result result = analyze("The trial covered 200 tests. The service uses PostgreSQL 16.");

		assertThat(result.evidenceScore()).isEqualTo(0.5);
		assertThat(result.concretenessScore()).isZero();
	}

	@Test
	void doesNotTreatPlainNumbersOrFileNamesAsEvidence() {
		SlopRule.Result result = analyze("The project uses PostgreSQL 16 and pom.xml.");

		assertThat(result.evidenceScore()).isZero();
	}

	private static SlopRule.Result analyze(String text) {
		return new EvidenceRule()
				.analyze(SlopContext.create(text, new ResourceDictionaryLoader().load(Language.ENGLISH)), 120);
	}
}
