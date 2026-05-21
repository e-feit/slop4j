package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.Language;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class LanguageParser {

	public List<Language> parse(List<String> values) {
		Objects.requireNonNull(values, "values");
		List<Language> languages = new ArrayList<>();
		for (String value : values) {
			if (value == null || value.isBlank()) {
				continue;
			}
			languages.add(parseOne(value.strip()));
		}
		if (languages.isEmpty()) {
			throw new IllegalArgumentException("At least one language must be configured.");
		}
		return List.copyOf(languages);
	}

	private static Language parseOne(String value) {
		return switch (value.toLowerCase(Locale.ROOT)) {
			case "en", "english" -> Language.ENGLISH;
			case "de", "german", "deutsch" -> Language.GERMAN;
			default -> throw new IllegalArgumentException("Unsupported slop4j language: " + value);
		};
	}
}
