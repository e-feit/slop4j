package dev.feit.slop4j.internal.rules;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import org.junit.jupiter.api.Test;

class ConcreteAnchorRuleTest {

	@Test
	void recognizesTechnicalAnchors() {
		SlopRule.Result result = new ConcreteAnchorRule().analyze(SlopContext.create("""
				mvn clean test
				Configure PaymentService in pom.xml for PostgreSQL 16.
				Call validateRequest(input).
				""", new ResourceDictionaryLoader().load(Language.ENGLISH)), 120);

		assertThat(result.evidenceScore()).isGreaterThan(0.7);
		assertThat(result.concretenessScore()).isGreaterThan(0.7);
	}
}
