package dev.feit.slop4j.cli.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import dev.feit.slop4j.Language;
import org.junit.jupiter.api.Test;

class LanguageParserTest {

	@Test
	void parsesSupportedLanguageCodes() {
		assertThat(new LanguageParser().parse("en,deutsch,german")).containsExactly(Language.ENGLISH, Language.GERMAN,
				Language.GERMAN);
	}

	@Test
	void rejectsBlankLanguageList() {
		assertThatIllegalArgumentException().isThrownBy(() -> new LanguageParser().parse(" , "))
				.withMessage("At least one language must be configured.");
	}

	@Test
	void rejectsUnsupportedLanguage() {
		assertThatIllegalArgumentException().isThrownBy(() -> new LanguageParser().parse("fr"))
				.withMessage("Unsupported slop4j language: fr");
	}
}
