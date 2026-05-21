package dev.feit.slop4j.cli.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConsoleReporterTest {

	@Test
	void reportsSuccessfulSummary() {
		StringWriter output = new StringWriter();
		ConsoleReporter reporter = new ConsoleReporter(new PrintWriter(output));

		reporter.reportSummary(summaryWithoutViolations(), configuration());

		assertThat(output.toString()).contains("Slop4J Audit").contains("Scanned 1 file(s).")
				.contains("OK README.md slopScore=31.2 verdict=ACCEPTABLY_FLUFFY findings=0")
				.contains("Slop4J Audit completed without policy violations.");
	}

	@Test
	void reportsNoFilesPolicyViolation() {
		StringWriter output = new StringWriter();
		ConsoleReporter reporter = new ConsoleReporter(new PrintWriter(output));

		reporter.reportNoFiles(true);

		assertThat(output.toString()).contains("No files matched the configured slop audit includes.")
				.contains("Policy violation: no files matched and fail-if-no-files is enabled.");
	}

	private static AuditSummary summaryWithoutViolations() {
		SlopReport report = new SlopReport(31.2, 0.0, 0.0, 0.8, 0.7, 0.6, 0.0, 0.0, SlopVerdict.ACCEPTABLY_FLUFFY,
				List.of());
		FileSlopResult result = new FileSlopResult(Path.of("README.md"), report);
		return new AuditSummary(List.of(result), List.of(new AuditDecision(result, false, false)));
	}

	private static AuditConfiguration configuration() {
		return AuditConfiguration.create(Path.of("."), List.of("README.md"), null, 60.0, 80.0, true, false, false, 5,
				120);
	}
}
