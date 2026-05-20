package dev.feit.slop4j.examples;

import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import java.util.Locale;

public final class QualityGateExample {

	private static final double DEFAULT_MAX_SLOP_SCORE = 60.0;

	private final SlopAnalyzer analyzer;
	private final double maxSlopScore;

	public QualityGateExample() {
		this(DEFAULT_MAX_SLOP_SCORE);
	}

	public QualityGateExample(double maxSlopScore) {
		this(SlopAnalyzer.builder().build(), maxSlopScore);
	}

	QualityGateExample(SlopAnalyzer analyzer, double maxSlopScore) {
		if (maxSlopScore < 0.0 || maxSlopScore > 100.0) {
			throw new IllegalArgumentException("Maximum slop score must be between 0.0 and 100.0.");
		}
		this.analyzer = analyzer;
		this.maxSlopScore = maxSlopScore;
	}

	public Result evaluate(String text) {
		SlopReport report = analyzer.analyze(text);
		if (report.slopScore() > maxSlopScore) {
			return new Result(false, String.format(Locale.ROOT, "Slop score %.1f exceeds configured limit %.1f.",
					report.slopScore(), maxSlopScore), report);
		}

		return new Result(true, "Text passed deterministic slop quality gate.", report);
	}

	public static void main(String[] args) {
		Result result = new QualityGateExample().evaluate("""
				Create a Java 17 service with one POST /payments endpoint.
				Persist requests in PostgreSQL 16 and expose p95 latency via Micrometer.
				""");

		System.out.printf(Locale.ROOT, "Accepted: %s%n", result.accepted());
		System.out.printf(Locale.ROOT, "Reason: %s%n", result.reason());
		System.out.printf(Locale.ROOT, "Slop score: %.1f%n", result.report().slopScore());
	}

	public record Result(boolean accepted, String reason, SlopReport report) {
	}
}
