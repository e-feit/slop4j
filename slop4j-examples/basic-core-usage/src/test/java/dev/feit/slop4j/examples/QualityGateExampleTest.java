package dev.feit.slop4j.examples;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QualityGateExampleTest {

	@Test
	void acceptsConcreteActionableText() {
		QualityGateExample.Result result = new QualityGateExample().evaluate("""
				Create a Java 17 service with one POST /payments endpoint.
				Persist requests in PostgreSQL 16 and expose p95 latency via Micrometer.
				""");

		assertThat(result.accepted()).isTrue();
		assertThat(result.reason()).isEqualTo("Text passed deterministic slop quality gate.");
	}

	@Test
	void rejectsTextAboveConfiguredSlopScoreLimit() {
		QualityGateExample.Result result = new QualityGateExample(40.0).evaluate("""
				We leverage agentic AI to unlock seamless enterprise-grade transformation.
				Our robust platform enables scalable innovation and future-proof orchestration.
				The holistic strategy accelerates optimization, modernization and alignment.
				""");

		assertThat(result.accepted()).isFalse();
		assertThat(result.reason()).contains("Slop score");
	}
}
