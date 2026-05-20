package dev.feit.slop4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class SlopAnalyzerTest {

	@Test
	void reportsLowScoreForConcreteTechnicalEnglishText() {
		SlopReport report = SlopAnalyzer.builder().build().analyze("""
				Create a Spring Boot service with one POST /payments endpoint.
				Persist requests in PostgreSQL 16 using Flyway migrations.
				Expose p95 latency and error rate via Micrometer.
				""");

		assertThat(report.slopScore()).isLessThanOrEqualTo(30.0);
		assertThat(report.verdict()).isIn(SlopVerdict.CLEAN, SlopVerdict.DANGEROUSLY_USEFUL);
		assertThat(report.evidenceScore()).isGreaterThan(0.5);
		assertThat(report.actionabilityScore()).isGreaterThan(0.3);
	}

	@Test
	void reportsHighScoreForEnglishSlopText() {
		SlopReport report = SlopAnalyzer.builder().build().analyze("""
				We leverage agentic AI to unlock seamless enterprise-grade transformation across modern workflows.
				Our robust platform enables scalable innovation and future-proof orchestration.
				The holistic strategy accelerates optimization, modernization, resilience and alignment.
				In many cases it can help depending on your needs.
				""");

		assertThat(report.slopScore()).isGreaterThanOrEqualTo(65.0);
		assertThat(report.verdict()).isIn(SlopVerdict.LINKEDIN_READY, SlopVerdict.PREMIUM_POLISHED_GARBAGE,
				SlopVerdict.BOARD_APPROVED_SLOP, SlopVerdict.GARBAGE_IN_SLOP_OUT, SlopVerdict.CERTIFIED_BRAINLESS_SLOP);
		assertThat(report.findings()).extracting(SlopFinding::type).contains(SlopFindingType.BUZZWORD_DENSITY,
				SlopFindingType.VAGUE_PHRASE, SlopFindingType.LOW_CONCRETENESS);
	}

	@Test
	void reportsHighScoreForGermanSlopTextWhenGermanIsEnabled() {
		SlopAnalyzer analyzer = SlopAnalyzer.builder().languages(Language.ENGLISH, Language.GERMAN).build();

		SlopReport report = analyzer.analyze(
				"""
						Unsere KI-gestützte Plattform ermöglicht eine nahtlose und skalierbare Transformation moderner Arbeitsabläufe.
						Sie schafft Synergie, optimiert Innovation und verbessert die Effizienz durch ganzheitliche Orchestrierung.
						Die robuste Strategie stärkt Modernisierung, Resilienz, Agilität und Ausrichtung.
						Generell kann sie helfen, abhängig von den Anforderungen.
						""");

		assertThat(report.slopScore()).isGreaterThanOrEqualTo(65.0);
	}

	@Test
	void reportsLowScoreForGermanTechnicalTextWhenGermanIsEnabled() {
		SlopReport report = SlopAnalyzer.builder().language(Language.GERMAN).build().analyze("""
				Erstelle einen Spring Boot Service mit einem POST /payments Endpoint.
				Speichere Requests in PostgreSQL 16 und versioniere das Schema mit Flyway.
				Überwache p95-Latenz und Fehlerrate über Micrometer.
				""");

		assertThat(report.slopScore()).isLessThanOrEqualTo(35.0);
	}

	@Test
	void exposesAllStepOneMetricsInReport() {
		SlopReport report = new SlopReport(12.3, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, SlopVerdict.CLEAN, List.of());

		assertThat(report.slopScore()).isEqualTo(12.3);
		assertThat(report.buzzwordDensity()).isEqualTo(0.1);
		assertThat(report.vaguePhraseDensity()).isEqualTo(0.2);
		assertThat(report.concretenessScore()).isEqualTo(0.3);
		assertThat(report.actionabilityScore()).isEqualTo(0.4);
		assertThat(report.evidenceScore()).isEqualTo(0.5);
		assertThat(report.repetitionScore()).isEqualTo(0.6);
		assertThat(report.overconfidenceScore()).isEqualTo(0.7);
	}

	@Test
	void treatsNullTextLikeEmptyText() {
		SlopReport report = SlopAnalyzer.builder().build().analyze(null);

		assertThat(report.slopScore()).isZero();
		assertThat(report.findings()).isEmpty();
	}
}
