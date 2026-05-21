package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.feit.slop4j.Language;
import java.util.List;
import org.junit.jupiter.api.Test;

class LanguageParserTest {

	@Test
	void parsesEnglishAliases() {
		assertThat(new LanguageParser().parse(List.of("en", "english"))).containsExactly(Language.ENGLISH,
				Language.ENGLISH);
	}

	@Test
	void parsesGermanAliases() {
		assertThat(new LanguageParser().parse(List.of("de", "german", "deutsch"))).containsExactly(Language.GERMAN,
				Language.GERMAN, Language.GERMAN);
	}

	@Test
	void trimsAndIgnoresBlankValues() {
		assertThat(new LanguageParser().parse(List.of(" en ", "", "  ", "DE"))).containsExactly(Language.ENGLISH,
				Language.GERMAN);
	}

	@Test
	void rejectsUnsupportedLanguage() {
		assertThatThrownBy(() -> new LanguageParser().parse(List.of("fr"))).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Unsupported slop4j language: fr");
	}

	@Test
	void rejectsEmptyEffectiveLanguageList() {
		assertThatThrownBy(() -> new LanguageParser().parse(List.of("", " ")))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("At least one language must be configured.");
	}
}
