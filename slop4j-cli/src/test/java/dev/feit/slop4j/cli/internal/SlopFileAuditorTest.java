package dev.feit.slop4j.cli.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlopFileAuditorTest {

	@Test
	void auditsFilesWithConfiguredLanguages(@TempDir Path tempDir) throws IOException {
		Path file = tempDir.resolve("README.md");
		Files.writeString(file, "We leverage agentic AI to unlock seamless enterprise-grade transformation.");
		AuditConfiguration configuration = AuditConfiguration
				.create(tempDir, List.of("README.md"), null, 100.0, 80.0, true, false, false, 5, 80)
				.withLanguages(List.of(Language.ENGLISH));

		List<FileSlopResult> results = new SlopFileAuditor(configuration).audit(List.of(file));

		assertThat(results).hasSize(1);
		assertThat(results.get(0).displayPath()).isEqualTo("README.md");
		assertThat(results.get(0).report().slopScore()).isGreaterThan(0.0);
	}
}
