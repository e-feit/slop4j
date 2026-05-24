package dev.feit.slop4j.spring.boot.autoconfigure;

import dev.feit.slop4j.Language;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

final class LanguageCodeParser {

	private LanguageCodeParser() {
	}

	static List<Language> parseAll(Collection<String> languageCodes) {
		List<Language> languages = new ArrayList<>();
		for (String languageCode : languageCodes) {
			languages.add(parse(languageCode));
		}
		return List.copyOf(languages);
	}

	private static Language parse(String languageCode) {
		String normalized = languageCode.strip().toLowerCase(Locale.ROOT);
		if (normalized.isEmpty()) {
			throw new IllegalArgumentException("Language code must not be blank.");
		}
		return switch (normalized) {
			case "en", "english" -> Language.ENGLISH;
			case "de", "german", "deutsch" -> Language.GERMAN;
			default -> throw new IllegalArgumentException("Unsupported slop4j language: " + languageCode);
		};
	}
}
