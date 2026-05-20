package dev.feit.slop4j.examples;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.SlopReport;
import org.junit.jupiter.api.Test;

class BasicAnalysisExampleTest {

	@Test
	void analyzesEnglishTextAndExposesReportMetrics() {
		SlopReport report = new BasicAnalysisExample().analyze("""
				We leverage agentic AI to unlock seamless enterprise-grade transformation.
				Our robust platform enables scalable innovation and future-proof orchestration.
				""");

		assertThat(report.slopScore()).isGreaterThan(50.0);
		assertThat(report.findings()).extracting(SlopFinding::type).contains(SlopFindingType.BUZZWORD_DENSITY);
	}
}
