package dev.feit.slop4j.maven.plugin;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlopAuditMojoTest {

	@TempDir
	Path tempDir;

	@Test
	void skipsExecutionWhenConfigured() throws Exception {
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "skip", true);

		assertThatCode(mojo::execute).doesNotThrowAnyException();
	}

	@Test
	void succeedsWhenNoFilesMatchAndFailureDisabled() throws Exception {
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("docs/**/*.md"));
		set(mojo, "failIfNoFiles", false);

		assertThatCode(mojo::execute).doesNotThrowAnyException();
	}

	@Test
	void failsWhenNoFilesMatchAndFailureEnabled() throws Exception {
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("docs/**/*.md"));
		set(mojo, "failIfNoFiles", true);

		assertThatThrownBy(mojo::execute).isInstanceOf(MojoExecutionException.class)
				.hasMessage("No files matched the configured slop audit includes.");
	}

	@Test
	void failsWhenMaxSlopScoreIsExceeded() throws Exception {
		Files.writeString(tempDir.resolve("README.md"),
				"We leverage agentic AI to unlock seamless enterprise-grade transformation across modern workflows.");
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("README.md"));
		set(mojo, "maxSlopScore", 10.0);
		set(mojo, "failOnSlop", true);

		assertThatThrownBy(mojo::execute).isInstanceOf(MojoExecutionException.class)
				.hasMessageContaining("exceeded maxSlopScore=10.0");
	}

	@Test
	void failsWhenTooConcreteModeIsViolated() throws Exception {
		Files.createDirectories(tempDir.resolve("docs"));
		Files.writeString(tempDir.resolve("docs/architecture.md"),
				"Create PaymentController.java. Run mvn test. Store PostgreSQL 16 migrations in db/migration.");
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("docs/**/*.md"));
		set(mojo, "failOnSlop", false);
		set(mojo, "failIfTooConcrete", true);
		set(mojo, "minSlopScore", 99.0);

		assertThatThrownBy(mojo::execute).isInstanceOf(MojoExecutionException.class)
				.hasMessageContaining("below minSlopScore=99.0");
	}

	private static void set(Object target, String fieldName, Object value) throws Exception {
		Field field = SlopAuditMojo.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}
