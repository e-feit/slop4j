package dev.feit.slop4j.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class Slop4jShellScriptTest {

	@Test
	void shellWrapperBuildsMissingJarAndForwardsArguments() throws IOException {
		Path script = repositoryRoot().resolve("scripts/slop4j");

		assertThat(script).exists().isExecutable();
		String content = Files.readString(script);
		assertThat(content).contains("SLOP4J_REBUILD").contains("mvn -q -B -pl slop4j-cli -am package")
				.contains("java -jar").contains("\"$@\"");
		assertThat(content).doesNotContain("slop4j-cli-0.1.0-SNAPSHOT.jar").contains("slop4j-cli-*.jar");
	}

	private static Path repositoryRoot() {
		Path current = Path.of("").toAbsolutePath().normalize();
		if (current.getFileName().toString().equals("slop4j-cli")) {
			return current.getParent();
		}
		return current;
	}
}
