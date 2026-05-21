package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.feit.slop4j.Language;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditConfigurationTest {

	@Test
	void appliesDefaultsForNullLists() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null, null,
				null, false, 5, 120);

		assertThat(configuration.languages()).containsExactly(Language.ENGLISH);
		assertThat(configuration.includes()).containsExactly("README.md", "README_DE.md", "docs/**/*.md",
				"adr/**/*.md");
		assertThat(configuration.excludes()).containsExactly("target/**", ".git/**");
	}

	@Test
	void trimsIncludesAndExcludes() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false,
				List.of("de"), List.of(" README.md ", "docs/**/*.md"), List.of(" target/** "), false, 5, 120);

		assertThat(configuration.languages()).containsExactly(Language.GERMAN);
		assertThat(configuration.includes()).containsExactly("README.md", "docs/**/*.md");
		assertThat(configuration.excludes()).containsExactly("target/**");
	}

	@Test
	void rejectsInvalidMaxSlopScore() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 100.1, 80.0, true, false, null, null, null,
				false, 5, 120)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("maxSlopScore must be between 0.0 and 100.0.");
	}

	@Test
	void rejectsInvalidMinSlopScore() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 60.0, -0.1, true, false, null, null, null,
				false, 5, 120)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("minSlopScore must be between 0.0 and 100.0.");
	}

	@Test
	void rejectsEmptyIncludes() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null,
				List.of("", " "), null, false, 5, 120)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("At least one include pattern must be configured.");
	}

	@Test
	void rejectsNegativeFindingLimit() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null, null, null,
				false, -1, 120)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("maxFindingsPerFile must not be negative.");
	}

	@Test
	void rejectsNegativeEvidenceLength() {
		assertThatThrownBy(
				() -> AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null, null, null, false, 5, -1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("maxFindingEvidenceLength must not be negative.");
	}
}
