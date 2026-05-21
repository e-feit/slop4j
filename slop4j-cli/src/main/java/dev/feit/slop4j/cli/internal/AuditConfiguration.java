package dev.feit.slop4j.cli.internal;

import dev.feit.slop4j.Language;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record AuditConfiguration(Path baseDirectory, List<String> includes, List<String> excludes,
		List<Language> languages, double maxSlopScore, double minSlopScore, boolean failOnSlop,
		boolean failIfTooConcrete, boolean failIfNoFiles, int maxFindingsPerFile, int maxFindingEvidenceLength) {

	private static final List<String> DEFAULT_INCLUDES = List.of("README.md", "README_DE.md", "docs/**/*.md",
			"adr/**/*.md");
	private static final List<String> DEFAULT_EXCLUDES = List.of("target/**", ".git/**");

	public AuditConfiguration {
		baseDirectory = Objects.requireNonNull(baseDirectory, "baseDirectory").toAbsolutePath().normalize();
		includes = List.copyOf(Objects.requireNonNull(includes, "includes"));
		excludes = List.copyOf(Objects.requireNonNull(excludes, "excludes"));
		languages = List.copyOf(Objects.requireNonNull(languages, "languages"));
	}

	public static AuditConfiguration create(Path baseDirectory, List<String> includes, @Nullable List<String> excludes,
			double maxSlopScore, double minSlopScore, boolean failOnSlop, boolean failIfTooConcrete,
			boolean failIfNoFiles, int maxFindingsPerFile, int maxFindingEvidenceLength) {
		if (!isScore(maxSlopScore)) {
			throw new IllegalArgumentException("max-score must be between 0.0 and 100.0.");
		}
		if (!isScore(minSlopScore)) {
			throw new IllegalArgumentException("min-score must be between 0.0 and 100.0.");
		}
		if (maxFindingsPerFile < 0) {
			throw new IllegalArgumentException("max-findings must not be negative.");
		}
		if (maxFindingEvidenceLength < 0) {
			throw new IllegalArgumentException("max-evidence-length must not be negative.");
		}
		List<String> normalizedIncludes = normalizePatterns(includes.isEmpty() ? DEFAULT_INCLUDES : includes);
		if (normalizedIncludes.isEmpty()) {
			throw new IllegalArgumentException("At least one include pattern must be configured.");
		}
		List<String> normalizedExcludes = normalizePatterns(excludes == null ? DEFAULT_EXCLUDES : excludes);
		return new AuditConfiguration(baseDirectory, normalizedIncludes, normalizedExcludes, List.of(Language.ENGLISH),
				maxSlopScore, minSlopScore, failOnSlop, failIfTooConcrete, failIfNoFiles, maxFindingsPerFile,
				maxFindingEvidenceLength);
	}

	public AuditConfiguration withLanguages(List<Language> languages) {
		if (languages.isEmpty()) {
			throw new IllegalArgumentException("At least one language must be configured.");
		}
		return new AuditConfiguration(baseDirectory, includes, excludes, languages, maxSlopScore, minSlopScore,
				failOnSlop, failIfTooConcrete, failIfNoFiles, maxFindingsPerFile, maxFindingEvidenceLength);
	}

	private static boolean isScore(double value) {
		return !Double.isNaN(value) && !Double.isInfinite(value) && value >= 0.0 && value <= 100.0;
	}

	private static List<String> normalizePatterns(List<String> patterns) {
		return patterns.stream().filter(Objects::nonNull).map(String::strip).filter(pattern -> !pattern.isBlank())
				.toList();
	}
}
