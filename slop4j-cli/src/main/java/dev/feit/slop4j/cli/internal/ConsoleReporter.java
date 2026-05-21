package dev.feit.slop4j.cli.internal;

import dev.feit.slop4j.SlopFinding;
import java.io.PrintWriter;
import java.util.Objects;

public final class ConsoleReporter {

	private final PrintWriter out;

	public ConsoleReporter(PrintWriter out) {
		this.out = Objects.requireNonNull(out, "out");
	}

	public void reportNoFiles(boolean failIfNoFiles) {
		out.println("Slop4J Audit");
		out.println("No files matched the configured slop audit includes.");
		if (failIfNoFiles) {
			out.println("Policy violation: no files matched and fail-if-no-files is enabled.");
		}
		out.flush();
	}

	public void reportSummary(AuditSummary summary, AuditConfiguration configuration) {
		out.println("Slop4J Audit");
		out.println("Scanned " + summary.scannedFileCount() + " file(s).");
		for (AuditDecision decision : summary.decisions()) {
			reportDecision(decision, configuration);
		}
		if (summary.maximumSlopViolationCount() > 0) {
			out.println("Policy violation: " + summary.maximumSlopViolationCount() + " file(s) exceeded max-score="
					+ configuration.maxSlopScore() + ".");
		}
		if (summary.minimumSlopViolationCount() > 0) {
			out.println("Policy violation: " + summary.minimumSlopViolationCount() + " file(s) were below min-score="
					+ configuration.minSlopScore() + ".");
		}
		if (!summary.hasPolicyViolations()) {
			out.println("Slop4J Audit completed without policy violations.");
		}
		out.flush();
	}

	private void reportDecision(AuditDecision decision, AuditConfiguration configuration) {
		FileSlopResult result = decision.result();
		if (decision.exceedsMaximumSlop()) {
			out.println(summaryLine("SLOP", result));
			result.report().findings().stream().limit(configuration.maxFindingsPerFile()).forEach(this::printFinding);
		} else if (decision.belowMinimumSlop()) {
			out.println("TOO_CONCRETE " + result.displayPath() + " slopScore=" + result.report().slopScore()
					+ " is below min-score=" + configuration.minSlopScore() + ".");
			out.println("  verdict=" + result.report().verdict());
			out.println("  This may reduce strategic optionality.");
		} else {
			out.println(summaryLine("OK", result));
		}
	}

	private static String summaryLine(String prefix, FileSlopResult result) {
		return prefix + " " + result.displayPath() + " slopScore=" + result.report().slopScore() + " verdict="
				+ result.report().verdict() + " findings=" + result.report().findings().size();
	}

	private void printFinding(SlopFinding finding) {
		String evidence = finding.evidence().isBlank() ? "" : " Evidence: " + finding.evidence();
		out.println("  " + finding.type() + " " + finding.severity() + ": " + finding.message() + evidence);
	}
}
