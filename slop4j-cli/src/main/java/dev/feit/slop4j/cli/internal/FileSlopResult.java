package dev.feit.slop4j.cli.internal;

import dev.feit.slop4j.SlopReport;
import java.nio.file.Path;
import java.util.Objects;

public record FileSlopResult(Path relativePath, SlopReport report) {

	public FileSlopResult {
		Objects.requireNonNull(relativePath, "relativePath");
		Objects.requireNonNull(report, "report");
	}

	public String displayPath() {
		return relativePath.toString().replace('\\', '/');
	}
}
