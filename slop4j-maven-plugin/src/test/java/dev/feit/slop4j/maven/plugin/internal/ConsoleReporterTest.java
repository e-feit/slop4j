package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

class ConsoleReporterTest {

	@Test
	void reportsMaximumSlopViolationWithFindings() {
		CapturingLog log = new CapturingLog();
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null, null,
				null, false, 1, 120);
		FileSlopResult result = new FileSlopResult(Path.of("README.md"),
				new SlopReport(72.8, 0.2, 0.0, 0.1, 0.1, 0.0, 0.0, 0.0, SlopVerdict.LINKEDIN_READY,
						List.of(new SlopFinding(SlopFindingType.BUZZWORD_DENSITY, Severity.WARNING,
								"Buzzword density is suspiciously high.", "agentic"))));
		AuditSummary summary = new AuditSummary(List.of(result), List.of(new AuditDecision(result, true, false)));

		new ConsoleReporter(log).reportSummary(summary, configuration);

		assertThat(log.info).contains("Slop4J Audit", "Scanned 1 file(s).");
		assertThat(log.warn).anyMatch(line -> line.contains("README.md slopScore=72.8 verdict=LINKEDIN_READY"));
		assertThat(log.warn).anyMatch(line -> line.contains("BUZZWORD_DENSITY WARNING"));
		assertThat(log.error).contains("Build failed because 1 file(s) exceeded maxSlopScore=60.0.");
	}

	@Test
	void reportsTooConcreteViolation() {
		CapturingLog log = new CapturingLog();
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, false, true, null, null,
				null, false, 5, 120);
		FileSlopResult result = new FileSlopResult(Path.of("docs/architecture.md"),
				new SlopReport(13.7, 0.0, 0.0, 0.9, 0.8, 0.9, 0.0, 0.0, SlopVerdict.DANGEROUSLY_USEFUL, List.of()));
		AuditSummary summary = new AuditSummary(List.of(result), List.of(new AuditDecision(result, false, true)));

		new ConsoleReporter(log).reportSummary(summary, configuration);

		assertThat(log.error).anyMatch(line -> line.contains("docs/architecture.md is dangerously specific."));
		assertThat(log.error).anyMatch(line -> line.contains("slopScore=13.7 is below minSlopScore=80.0."));
	}

	private static final class CapturingLog implements Log {

		private final List<String> debug = new ArrayList<>();
		private final List<String> info = new ArrayList<>();
		private final List<String> warn = new ArrayList<>();
		private final List<String> error = new ArrayList<>();

		@Override
		public boolean isDebugEnabled() {
			return true;
		}

		@Override
		public void debug(CharSequence content) {
			debug.add(content.toString());
		}

		@Override
		public void debug(CharSequence content, Throwable error) {
			debug.add(content.toString());
		}

		@Override
		public void debug(Throwable error) {
			debug.add(error.toString());
		}

		@Override
		public boolean isInfoEnabled() {
			return true;
		}

		@Override
		public void info(CharSequence content) {
			info.add(content.toString());
		}

		@Override
		public void info(CharSequence content, Throwable error) {
			info.add(content.toString());
		}

		@Override
		public void info(Throwable error) {
			info.add(error.toString());
		}

		@Override
		public boolean isWarnEnabled() {
			return true;
		}

		@Override
		public void warn(CharSequence content) {
			warn.add(content.toString());
		}

		@Override
		public void warn(CharSequence content, Throwable error) {
			warn.add(content.toString());
		}

		@Override
		public void warn(Throwable error) {
			warn.add(error.toString());
		}

		@Override
		public boolean isErrorEnabled() {
			return true;
		}

		@Override
		public void error(CharSequence content) {
			error.add(content.toString());
		}

		@Override
		public void error(CharSequence content, Throwable error) {
			this.error.add(content.toString());
		}

		@Override
		public void error(Throwable error) {
			this.error.add(error.toString());
		}
	}
}
