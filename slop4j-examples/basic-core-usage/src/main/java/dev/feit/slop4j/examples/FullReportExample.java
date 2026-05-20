package dev.feit.slop4j.examples;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopReport;
import java.util.Formatter;
import java.util.Locale;

public final class FullReportExample {

	private static final String SAMPLE_TEXT = """
			We leverage agentic AI to unlock seamless enterprise-grade transformation.
			Our robust platform enables scalable innovation and future-proof orchestration.

			Unsere KI-gestützte Plattform ermöglicht eine nahtlose und skalierbare Transformation.
			Sie schafft Synergie, optimiert Innovation und verbessert die Effizienz.
			""";

	private FullReportExample() {
	}

	public static void main(String[] args) {
		System.out.print(formatReport(analyzeSampleText()));
	}

	static SlopReport analyzeSampleText() {
		return SlopAnalyzer.builder().languages(Language.ENGLISH, Language.GERMAN).build().analyze(SAMPLE_TEXT);
	}

	static String formatReport(SlopReport report) {
		StringBuilder output = new StringBuilder();
		try (Formatter formatter = new Formatter(output, Locale.ROOT)) {
			formatter.format("Slop report%n");
			formatter.format("===========%n");
			formatter.format("Slop score: %.1f%n", report.slopScore());
			formatter.format("Verdict: %s%n%n", report.verdict());
			formatter.format("Score components%n");
			formatter.format("----------------%n");
			formatter.format("Buzzword density: %.3f%n", report.buzzwordDensity());
			formatter.format("Vague phrase density: %.3f%n", report.vaguePhraseDensity());
			formatter.format("Concreteness score: %.3f%n", report.concretenessScore());
			formatter.format("Actionability score: %.3f%n", report.actionabilityScore());
			formatter.format("Evidence score: %.3f%n", report.evidenceScore());
			formatter.format("Repetition score: %.3f%n", report.repetitionScore());
			formatter.format("Overconfidence score: %.3f%n%n", report.overconfidenceScore());
			formatter.format("Findings: %d%n", report.findings().size());

			int index = 1;
			for (SlopFinding finding : report.findings()) {
				formatter.format("%d. %s [%s]%n", index, finding.type(), finding.severity());
				formatter.format("   Message: %s%n", finding.message());
				if (finding.evidence().isBlank()) {
					formatter.format("   Evidence: <none>%n");
				} else {
					formatter.format("   Evidence: %s%n", finding.evidence());
				}
				index++;
			}
		}
		return output.toString();
	}
}
