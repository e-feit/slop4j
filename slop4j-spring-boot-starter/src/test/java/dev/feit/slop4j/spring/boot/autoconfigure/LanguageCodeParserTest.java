package dev.feit.slop4j.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.feit.slop4j.Language;
import java.util.List;
import org.junit.jupiter.api.Test;

class LanguageCodeParserTest {

	@Test
	void parsesSupportedLanguageAliases() {
		assertThat(LanguageCodeParser.parseAll(List.of("en", "english", "de", "german", "deutsch")))
				.containsExactly(Language.ENGLISH, Language.ENGLISH, Language.GERMAN, Language.GERMAN, Language.GERMAN);
	}

	@Test
	void trimsAndLowercasesLanguageCodes() {
		assertThat(LanguageCodeParser.parseAll(List.of(" EN ", "Deutsch"))).containsExactly(Language.ENGLISH,
				Language.GERMAN);
	}

	@Test
	void rejectsBlankLanguageCode() {
		assertThatThrownBy(() -> LanguageCodeParser.parseAll(List.of(" "))).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Language code must not be blank.");
	}

	@Test
	void rejectsUnsupportedLanguageCode() {
		assertThatThrownBy(() -> LanguageCodeParser.parseAll(List.of("fr")))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Unsupported slop4j language: fr");
	}
}
