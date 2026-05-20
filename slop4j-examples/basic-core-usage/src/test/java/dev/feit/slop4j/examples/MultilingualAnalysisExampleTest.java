package dev.feit.slop4j.examples;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.SlopReport;
import org.junit.jupiter.api.Test;

class MultilingualAnalysisExampleTest {

	@Test
	void analyzesGermanTextWhenGermanDictionaryIsEnabled() {
		SlopReport report = new MultilingualAnalysisExample().analyze("""
				Unsere KI-gestützte Plattform ermöglicht eine nahtlose und skalierbare Transformation.
				Sie schafft Synergie, optimiert Innovation und verbessert die Effizienz.
				""");

		assertThat(report.slopScore()).isGreaterThan(45.0);
		assertThat(report.findings()).isNotEmpty();
	}
}
