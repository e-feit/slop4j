package dev.feit.slop4j.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Slop4jCliTest {

	@Test
	void returnsSuccessForHelp() {
		StringWriter out = new StringWriter();
		StringWriter err = new StringWriter();

		int exitCode = new Slop4jCli(new PrintWriter(out), new PrintWriter(err)).run(new String[]{"--help"});

		assertThat(exitCode).isEqualTo(0);
		assertThat(out.toString()).contains("Usage:");
		assertThat(err.toString()).isEmpty();
	}

	@Test
	void returnsUsageErrorForUnknownOption() {
		StringWriter out = new StringWriter();
		StringWriter err = new StringWriter();

		int exitCode = new Slop4jCli(new PrintWriter(out), new PrintWriter(err))
				.run(new String[]{"audit", "--lagn", "de"});

		assertThat(exitCode).isEqualTo(2);
		assertThat(out.toString()).isEmpty();
		assertThat(err.toString()).contains("Unsupported option: --lagn");
	}

	@Test
	void returnsPolicyViolationForHighSlopFile(@TempDir Path tempDir) throws IOException {
		Files.writeString(tempDir.resolve("README.md"),
				"We leverage agentic AI to unlock seamless enterprise-grade transformation.");
		StringWriter out = new StringWriter();
		StringWriter err = new StringWriter();

		int exitCode = new Slop4jCli(new PrintWriter(out), new PrintWriter(err))
				.run(new String[]{"audit", "README.md", "--base-dir", tempDir.toString(), "--max-score", "1"});

		assertThat(exitCode).isEqualTo(1);
		assertThat(out.toString()).contains("Slop4J Audit").contains("Policy violation:");
		assertThat(err.toString()).isEmpty();
	}

	@Test
	void returnsPolicyViolationWhenNoFilesMatchAndFailureIsEnabled(@TempDir Path tempDir) {
		StringWriter out = new StringWriter();
		StringWriter err = new StringWriter();

		int exitCode = new Slop4jCli(new PrintWriter(out), new PrintWriter(err))
				.run(new String[]{"audit", "missing.md", "--base-dir", tempDir.toString(), "--fail-if-no-files"});

		assertThat(exitCode).isEqualTo(1);
		assertThat(out.toString()).contains("fail-if-no-files is enabled");
		assertThat(err.toString()).isEmpty();
	}
}
