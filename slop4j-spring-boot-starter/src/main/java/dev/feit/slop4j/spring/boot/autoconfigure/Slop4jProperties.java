package dev.feit.slop4j.spring.boot.autoconfigure;

import dev.feit.slop4j.Language;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for slop4j Spring Boot integration.
 */
@ConfigurationProperties("slop4j")
public class Slop4jProperties {

	/**
	 * Analyzer languages. Supported values are en, english, de, german and deutsch.
	 */
	private List<String> languages = new ArrayList<>(List.of("en"));

	/**
	 * Maximum evidence text length included in findings.
	 */
	private int maxFindingEvidenceLength = 120;

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(@Nullable List<String> languages) {
		if (languages == null || languages.isEmpty()) {
			throw new IllegalArgumentException("At least one slop4j language must be configured.");
		}
		this.languages = new ArrayList<>(languages);
	}

	public int getMaxFindingEvidenceLength() {
		return maxFindingEvidenceLength;
	}

	public void setMaxFindingEvidenceLength(int maxFindingEvidenceLength) {
		this.maxFindingEvidenceLength = maxFindingEvidenceLength;
	}

	List<Language> parsedLanguages() {
		return LanguageCodeParser.parseAll(languages);
	}
}
