package dev.feit.slop4j.cli.internal;

import dev.feit.slop4j.SlopAnalyzer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SlopFileAuditor {

	private final AuditConfiguration configuration;

	public SlopFileAuditor(AuditConfiguration configuration) {
		this.configuration = Objects.requireNonNull(configuration, "configuration");
	}

	public List<FileSlopResult> audit(List<Path> absoluteFiles) throws IOException {
		SlopAnalyzer analyzer = SlopAnalyzer.builder().languages(configuration.languages())
				.maxFindingEvidenceLength(configuration.maxFindingEvidenceLength()).build();
		List<FileSlopResult> results = new ArrayList<>();
		for (Path file : absoluteFiles) {
			String content = Files.readString(file, StandardCharsets.UTF_8);
			Path relativePath = configuration.baseDirectory().relativize(file.toAbsolutePath().normalize());
			results.add(new FileSlopResult(relativePath, analyzer.analyze(content)));
		}
		return List.copyOf(results);
	}
}
