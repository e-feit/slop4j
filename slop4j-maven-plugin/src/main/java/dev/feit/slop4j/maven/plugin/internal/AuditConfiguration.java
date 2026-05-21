package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.Language;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public record AuditConfiguration(Path baseDirectory, double maxSlopScore, double minSlopScore, boolean failOnSlop,
		boolean failIfTooConcrete, List<Language> languages, List<String> includes, List<String> excludes,
		boolean failIfNoFiles, int maxFindingsPerFile, int maxFindingEvidenceLength) {

	private static final List<String> DEFAULT_LANGUAGES = List.of("en");
	private static final List<String> DEFAULT_INCLUDES = List.of("README.md", "README_DE.md", "docs/**/*.md",
			"adr/**/*.md");
	private static final List<String> DEFAULT_EXCLUDES = List.of("target/**", ".git/**");

	public AuditConfiguration {
		Objects.requireNonNull(baseDirectory, "baseDirectory");
		languages = List.copyOf(Objects.requireNonNull(languages, "languages"));
		includes = List.copyOf(Objects.requireNonNull(includes, "includes"));
		excludes = List.copyOf(Objects.requireNonNull(excludes, "excludes"));
	}

	public static AuditConfiguration create(File basedir, double maxSlopScore, double minSlopScore, boolean failOnSlop,
			boolean failIfTooConcrete, List<String> languages, List<String> includes, List<String> excludes,
			boolean failIfNoFiles, int maxFindingsPerFile, int maxFindingEvidenceLength) {
		if (!isScore(maxSlopScore)) {
			throw new IllegalArgumentException("maxSlopScore must be between 0.0 and 100.0.");
		}
		if (!isScore(minSlopScore)) {
			throw new IllegalArgumentException("minSlopScore must be between 0.0 and 100.0.");
		}
		if (maxFindingsPerFile < 0) {
			throw new IllegalArgumentException("maxFindingsPerFile must not be negative.");
		}
		if (maxFindingEvidenceLength < 0) {
			throw new IllegalArgumentException("maxFindingEvidenceLength must not be negative.");
		}

		List<Language> parsedLanguages = new LanguageParser().parse(defaultIfNull(languages, DEFAULT_LANGUAGES));
		List<String> normalizedIncludes = normalizePatterns(defaultIfNull(includes, DEFAULT_INCLUDES));
		if (normalizedIncludes.isEmpty()) {
			throw new IllegalArgumentException("At least one include pattern must be configured.");
		}
		List<String> normalizedExcludes = normalizePatterns(defaultIfNull(excludes, DEFAULT_EXCLUDES));

		return new AuditConfiguration(Objects.requireNonNull(basedir, "basedir").toPath().toAbsolutePath().normalize(),
				maxSlopScore, minSlopScore, failOnSlop, failIfTooConcrete, parsedLanguages, normalizedIncludes,
				normalizedExcludes, failIfNoFiles, maxFindingsPerFile, maxFindingEvidenceLength);
	}

	private static boolean isScore(double value) {
		return !Double.isNaN(value) && !Double.isInfinite(value) && value >= 0.0 && value <= 100.0;
	}

	private static List<String> defaultIfNull(List<String> values, List<String> defaults) {
		return values == null ? defaults : values;
	}

	private static List<String> normalizePatterns(List<String> patterns) {
		return patterns.stream().filter(Objects::nonNull).map(String::strip).filter(pattern -> !pattern.isBlank())
				.toList();
	}
}
