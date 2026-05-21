package dev.feit.slop4j.cli.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AuditFileScannerTest {

	@Test
	void scansIncludedFilesAndAppliesExcludes(@TempDir Path tempDir) throws IOException {
		Files.writeString(tempDir.resolve("README.md"), "readme");
		Files.createDirectories(tempDir.resolve("docs/architecture"));
		Files.writeString(tempDir.resolve("docs/architecture/context.md"), "context");
		Files.createDirectories(tempDir.resolve("target"));
		Files.writeString(tempDir.resolve("target/generated.md"), "generated");

		List<Path> files = new AuditFileScanner(tempDir).scan(List.of("README.md", "docs/**/*.md", "target/**/*.md"),
				List.of("target/**"));

		assertThat(files).extracting(path -> tempDir.relativize(path).toString().replace('\\', '/'))
				.containsExactly("README.md", "docs/architecture/context.md");
	}

	@Test
	void supportsSingleDirectoryAndNestedGlobSemantics(@TempDir Path tempDir) throws IOException {
		Files.createDirectories(tempDir.resolve("docs/architecture"));
		Files.writeString(tempDir.resolve("docs/plan.md"), "plan");
		Files.writeString(tempDir.resolve("docs/architecture/context.md"), "context");

		List<Path> nestedFiles = new AuditFileScanner(tempDir).scan(List.of("docs/**/*.md"), List.of());
		List<Path> singleDirectoryFiles = new AuditFileScanner(tempDir).scan(List.of("docs/*.md"), List.of());

		assertThat(nestedFiles).extracting(path -> tempDir.relativize(path).toString().replace('\\', '/'))
				.containsExactly("docs/architecture/context.md", "docs/plan.md");
		assertThat(singleDirectoryFiles).extracting(path -> tempDir.relativize(path).toString().replace('\\', '/'))
				.containsExactly("docs/plan.md");
	}
}
