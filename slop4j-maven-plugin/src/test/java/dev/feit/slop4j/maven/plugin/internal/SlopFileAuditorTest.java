package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlopFileAuditorTest {

	@TempDir
	Path tempDir;

	@Test
	void auditsFilesWithConfiguredLanguages() throws Exception {
		Path readme = write("README.md",
				"Unsere KI-gestuetzte Plattform ermoeglicht eine nahtlose skalierbare Transformation.");
		AuditConfiguration configuration = AuditConfiguration.create(tempDir.toFile(), 60.0, 80.0, true, false,
				List.of("de"), List.of("README.md"), List.of(), false, 5, 120);

		List<FileSlopResult> results = new SlopFileAuditor(configuration).audit(List.of(readme));

		assertThat(results).hasSize(1);
		assertThat(results.get(0).relativePath().toString().replace('\\', '/')).isEqualTo("README.md");
		assertThat(results.get(0).report().slopScore()).isGreaterThan(0.0);
	}

	private Path write(String relativePath, String content) throws Exception {
		Path path = tempDir.resolve(relativePath);
		Files.createDirectories(path.getParent() == null ? tempDir : path.getParent());
		Files.writeString(path, content);
		return path;
	}
}
