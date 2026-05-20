package dev.feit.slop4j.internal.rules;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import org.junit.jupiter.api.Test;

class BuzzwordRuleTest {

	@Test
	void emitsFindingForHighBuzzwordDensity() {
		SlopRule.Result result = new BuzzwordRule()
				.analyze(SlopContext.create("agentic seamless enterprise-grade transformation",
						new ResourceDictionaryLoader().load(Language.ENGLISH)), 120);

		assertThat(result.findings()).extracting(finding -> finding.type()).contains(SlopFindingType.BUZZWORD_DENSITY);
		assertThat(result.buzzwordDensity()).isGreaterThanOrEqualTo(0.08);
	}

	@Test
	void doesNotEmitFindingForSingleBuzzwordInShortText() {
		SlopRule.Result result = new BuzzwordRule().analyze(
				SlopContext.create("agentic parser", new ResourceDictionaryLoader().load(Language.ENGLISH)), 120);

		assertThat(result.findings()).isEmpty();
	}
}
