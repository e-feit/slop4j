package dev.feit.slop4j.cli.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.feit.slop4j.Language;
import org.junit.jupiter.api.Test;

class CliParserTest {

	@Test
	void parsesAuditCommandWithPatternsAndOptions() {
		CliCommand command = new CliParser().parse(new String[]{"audit", "README.md", "docs/**/*.md", "--lang", "en,de",
				"--max-score", "55.5", "--exclude", "target/**,build/**", "--fail-if-too-concrete"});

		assertThat(command).isInstanceOf(AuditCommand.class);
		AuditConfiguration configuration = ((AuditCommand) command).configuration();
		assertThat(configuration.includes()).containsExactly("README.md", "docs/**/*.md");
		assertThat(configuration.excludes()).containsExactly("target/**", "build/**");
		assertThat(configuration.languages()).containsExactly(Language.ENGLISH, Language.GERMAN);
		assertThat(configuration.maxSlopScore()).isEqualTo(55.5);
		assertThat(configuration.failIfTooConcrete()).isTrue();
	}

	@Test
	void parsesTopLevelHelp() {
		assertThat(new CliParser().parse(new String[]{"--help"})).isInstanceOf(CliCommand.Help.class);
	}

	@Test
	void parsesAuditHelpAsHelp() {
		assertThat(new CliParser().parse(new String[]{"audit", "--help"})).isInstanceOf(CliCommand.Help.class);
	}

	@Test
	void rejectsUnknownOption() {
		assertThatExceptionOfType(CliException.class)
				.isThrownBy(() -> new CliParser().parse(new String[]{"audit", "--lagn", "de"}))
				.withMessage("Unsupported option: --lagn").extracting(CliException::exitCode)
				.isEqualTo(ExitCode.USAGE_ERROR);
	}

	@Test
	void rejectsMissingOptionValue() {
		assertThatExceptionOfType(CliException.class)
				.isThrownBy(() -> new CliParser().parse(new String[]{"audit", "--lang"}))
				.withMessage("Missing value for option: --lang");
	}

	@Test
	void rejectsInvalidIntegerOptionValue() {
		assertThatExceptionOfType(CliException.class)
				.isThrownBy(() -> new CliParser().parse(new String[]{"audit", "--max-findings", "abc"}))
				.withMessage("Invalid integer value for max-findings: abc");
	}
}
