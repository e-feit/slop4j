package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarkdownFileScannerTest {

	@TempDir
	Path tempDir;

	@Test
	void scansRootAndNestedMarkdownFiles() throws Exception {
		write("README.md");
		write("README_DE.md");
		write("docs/plan.md");
		write("docs/architecture/context.md");
		write("src/main/java/Example.java");

		List<Path> files = new MarkdownFileScanner(tempDir).scan(List.of("README.md", "README_DE.md", "docs/**/*.md"),
				List.of("target/**", ".git/**"));

		assertThat(relative(files)).containsExactly("README.md", "README_DE.md", "docs/architecture/context.md",
				"docs/plan.md");
	}

	@Test
	void excludesConfiguredPaths() throws Exception {
		write("README.md");
		write("target/generated.md");
		write(".git/ignored.md");

		List<Path> files = new MarkdownFileScanner(tempDir).scan(List.of("**/*.md", "README.md"),
				List.of("target/**", ".git/**"));

		assertThat(relative(files)).containsExactly("README.md");
	}

	@Test
	void singleStarDoesNotMatchNestedDirectories() throws Exception {
		write("docs/root.md");
		write("docs/nested/deep.md");

		List<Path> files = new MarkdownFileScanner(tempDir).scan(List.of("docs/*.md"), List.of());

		assertThat(relative(files)).containsExactly("docs/root.md");
	}

	private void write(String relativePath) throws Exception {
		Path path = tempDir.resolve(relativePath);
		Files.createDirectories(path.getParent() == null ? tempDir : path.getParent());
		Files.writeString(path, "content");
	}

	private List<String> relative(List<Path> files) {
		return files.stream().map(tempDir::relativize).map(Path::toString).map(path -> path.replace('\\', '/'))
				.toList();
	}
}
