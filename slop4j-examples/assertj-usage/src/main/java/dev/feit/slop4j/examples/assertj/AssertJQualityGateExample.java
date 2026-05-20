package dev.feit.slop4j.examples.assertj;

import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

import dev.feit.slop4j.Language;

public final class AssertJQualityGateExample {

    private AssertJQualityGateExample() {}

    public static String technicalDocumentation() {
        return """
                Create a Java 17 service with one POST /payments endpoint.
                Persist requests in PostgreSQL 16 using Flyway migrations.
                Add ./mvnw clean test to CI and expose p95 latency via Micrometer.
                """;
    }

    public static String strategyDeckNarrative() {
        return """
                We leverage agentic AI to unlock seamless enterprise-grade transformation across modern workflows.
                Our robust platform enables scalable innovation and future-proof orchestration for holistic modernization.
                The strategy accelerates optimization, resilience, alignment and transformation across every operating model.
                It is always guaranteed to deliver proven best-in-class outcomes in many cases.
                It can help organizations maximize impact depending on their evolving needs.
                """;
    }

    public static void main(String[] args) {
        assertThatSlop(technicalDocumentation(), Language.ENGLISH)
                .hasSlopScoreBelow(40.0)
                .hasActionabilityScoreAbove(0.3)
                .containsConcreteDetails()
                .doesNotSoundLikeLinkedInPost();

        assertThatSlop(strategyDeckNarrative(), Language.ENGLISH)
                .isBoardDeckReady()
                .containsNoImplementationDetails()
                .maximizesPlausibleDeniability();
    }
}
