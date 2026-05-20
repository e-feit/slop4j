package dev.feit.slop4j.examples;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.util.List;
import org.junit.jupiter.api.Test;

class FullReportExampleTest {

	@Test
	void formatsEveryReportMetricAndFindingEvidence() {
		SlopReport report = new SlopReport(72.34, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, SlopVerdict.LINKEDIN_READY,
				List.of(new SlopFinding(SlopFindingType.BUZZWORD_DENSITY, Severity.WARNING,
						"Buzzword density exceeds configured baseline.", "seamless innovation")));

		String formatted = FullReportExample.formatReport(report);

		assertThat(formatted).contains("Slop report");
		assertThat(formatted).contains("Slop score: 72.3");
		assertThat(formatted).contains("Verdict: LINKEDIN_READY");
		assertThat(formatted).contains("Buzzword density: 0.150");
		assertThat(formatted).contains("Vague phrase density: 0.250");
		assertThat(formatted).contains("Concreteness score: 0.350");
		assertThat(formatted).contains("Actionability score: 0.450");
		assertThat(formatted).contains("Evidence score: 0.550");
		assertThat(formatted).contains("Repetition score: 0.650");
		assertThat(formatted).contains("Overconfidence score: 0.750");
		assertThat(formatted).contains("Findings: 1");
		assertThat(formatted).contains("1. BUZZWORD_DENSITY [WARNING]");
		assertThat(formatted).contains("Message: Buzzword density exceeds configured baseline.");
		assertThat(formatted).contains("Evidence: seamless innovation");
	}

	@Test
	void analyzesBuiltInEnglishAndGermanSampleText() {
		SlopReport report = FullReportExample.analyzeSampleText();

		assertThat(report.slopScore()).isGreaterThan(50.0);
		assertThat(report.findings()).isNotEmpty();
		assertThat(report.findings()).extracting(SlopFinding::evidence)
				.anySatisfy(evidence -> assertThat(evidence).contains("seamless"))
				.anySatisfy(evidence -> assertThat(evidence).contains("effizienz"));
	}
}
