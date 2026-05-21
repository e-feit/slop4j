package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.SlopFinding;
import java.util.Objects;
import org.apache.maven.plugin.logging.Log;

public final class ConsoleReporter {

	private final Log log;

	public ConsoleReporter(Log log) {
		this.log = Objects.requireNonNull(log, "log");
	}

	public void reportSkipped() {
		log.info("Slop4J Audit skipped.");
	}

	public void reportNoFiles(boolean failIfNoFiles) {
		log.info("Slop4J Audit");
		if (failIfNoFiles) {
			log.error("No files matched the configured slop audit includes.");
		} else {
			log.warn("No files matched the configured slop audit includes.");
		}
	}

	public void reportSummary(AuditSummary summary, AuditConfiguration configuration) {
		log.info("Slop4J Audit");
		log.info("Scanned " + summary.scannedFileCount() + " file(s).");
		for (AuditDecision decision : summary.decisions()) {
			reportDecision(decision, configuration);
		}
		if (summary.maximumSlopViolationCount() > 0) {
			log.error("Build failed because " + summary.maximumSlopViolationCount() + " file(s) exceeded maxSlopScore="
					+ configuration.maxSlopScore() + ".");
		}
		if (summary.minimumSlopViolationCount() > 0) {
			log.error("Build failed because " + summary.minimumSlopViolationCount()
					+ " file(s) were below minSlopScore=" + configuration.minSlopScore() + ".");
		}
		if (!summary.hasPolicyViolations()) {
			log.info("Slop4J Audit completed without policy violations.");
		}
	}

	private void reportDecision(AuditDecision decision, AuditConfiguration configuration) {
		FileSlopResult result = decision.result();
		String line = result.displayPath() + " slopScore=" + result.report().slopScore() + " verdict="
				+ result.report().verdict() + " findings=" + result.report().findings().size();
		if (decision.exceedsMaximumSlop()) {
			log.warn(line);
			result.report().findings().stream().limit(configuration.maxFindingsPerFile()).forEach(this::warnFinding);
		} else if (decision.belowMinimumSlop()) {
			log.error(result.displayPath() + " is dangerously specific.");
			log.error("  slopScore=" + result.report().slopScore() + " is below minSlopScore="
					+ configuration.minSlopScore() + ".");
			log.error("  verdict=" + result.report().verdict());
			log.error("  This may reduce strategic optionality.");
		} else {
			log.info(line);
		}
	}

	private void warnFinding(SlopFinding finding) {
		String evidence = finding.evidence().isBlank() ? "" : " Evidence: " + finding.evidence();
		log.warn("  " + finding.type() + " " + finding.severity() + ": " + finding.message() + evidence);
	}
}
