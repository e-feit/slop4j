package dev.feit.slop4j.examples;

import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopReport;
import java.util.Locale;

public final class FindingEvidenceExample {

	private static final String SAMPLE_TEXT = """
			Our agentic platform unlocks robust, seamless transformation across the enterprise.
			It will certainly revolutionize every workflow and deliver unmatched innovation.
			""";

	private FindingEvidenceExample() {
	}

	public static void main(String[] args) {
		SlopAnalyzer analyzer = SlopAnalyzer.builder().maxFindingEvidenceLength(48).build();
		SlopReport report = analyzer.analyze(args.length > 0 ? String.join(" ", args) : SAMPLE_TEXT);

		for (SlopFinding finding : report.findings()) {
			System.out.printf(Locale.ROOT, "%s [%s]%n", finding.type(), finding.severity());
			System.out.printf(Locale.ROOT, "Message: %s%n", finding.message());
			if (!finding.evidence().isBlank()) {
				System.out.printf(Locale.ROOT, "Evidence: %s%n", finding.evidence());
			}
			System.out.println();
		}
	}
}
