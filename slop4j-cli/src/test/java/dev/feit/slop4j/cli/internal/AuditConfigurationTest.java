package dev.feit.slop4j.cli.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import dev.feit.slop4j.Language;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditConfigurationTest {

	@Test
	void createsDefaultAuditConfiguration() {
		AuditConfiguration configuration = AuditConfiguration.create(Path.of("."), List.of(), null, 60.0, 80.0, true,
				false, false, 5, 120);

		assertThat(configuration.includes()).containsExactly("README.md", "README_DE.md", "docs/**/*.md",
				"adr/**/*.md");
		assertThat(configuration.excludes()).containsExactly("target/**", ".git/**");
		assertThat(configuration.languages()).containsExactly(Language.ENGLISH);
		assertThat(configuration.failOnSlop()).isTrue();
	}

	@Test
	void rejectsInvalidScores() {
		assertThatIllegalArgumentException().isThrownBy(() -> AuditConfiguration.create(Path.of("."),
				List.of("README.md"), null, 101.0, 80.0, true, false, false, 5, 120))
				.withMessage("max-score must be between 0.0 and 100.0.");
	}

	@Test
	void rejectsNegativeFindingLimit() {
		assertThatIllegalArgumentException().isThrownBy(() -> AuditConfiguration.create(Path.of("."),
				List.of("README.md"), null, 60.0, 80.0, true, false, false, -1, 120))
				.withMessage("max-findings must not be negative.");
	}
}
