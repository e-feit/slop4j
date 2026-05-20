package dev.feit.slop4j.examples;

import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopReport;
import java.util.Locale;

public final class BasicAnalysisExample {

	private static final String SAMPLE_TEXT = """
			We leverage agentic AI to unlock seamless enterprise-grade transformation.
			Our robust platform enables scalable innovation and future-proof orchestration.
			""";

	private final SlopAnalyzer analyzer;

	public BasicAnalysisExample() {
		this(SlopAnalyzer.builder().build());
	}

	BasicAnalysisExample(SlopAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	public SlopReport analyze(String text) {
		return analyzer.analyze(text);
	}

	public static void main(String[] args) {
		printReport(new BasicAnalysisExample().analyze(SAMPLE_TEXT));
	}

	static void printReport(SlopReport report) {
		System.out.printf(Locale.ROOT, "Slop score: %.1f%n", report.slopScore());
		System.out.printf(Locale.ROOT, "Verdict: %s%n", report.verdict());
		System.out.printf(Locale.ROOT, "Findings: %d%n", report.findings().size());

		for (SlopFinding finding : report.findings()) {
			System.out.printf(Locale.ROOT, "- %s [%s]: %s%n", finding.type(), finding.severity(), finding.message());
		}
	}
}
