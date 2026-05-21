package dev.feit.slop4j.cli.internal;

import dev.feit.slop4j.Language;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

final class LanguageParser {

	List<Language> parse(String value) {
		Objects.requireNonNull(value, "value");
		List<Language> languages = new ArrayList<>();
		for (String part : value.split(",")) {
			String normalized = part.strip().toLowerCase(Locale.ROOT);
			if (normalized.isBlank()) {
				continue;
			}
			languages.add(parseOne(normalized, part.strip()));
		}
		if (languages.isEmpty()) {
			throw new IllegalArgumentException("At least one language must be configured.");
		}
		return List.copyOf(languages);
	}

	private static Language parseOne(String normalized, String original) {
		return switch (normalized) {
			case "en", "english" -> Language.ENGLISH;
			case "de", "german", "deutsch" -> Language.GERMAN;
			default -> throw new IllegalArgumentException("Unsupported slop4j language: " + original);
		};
	}
}
