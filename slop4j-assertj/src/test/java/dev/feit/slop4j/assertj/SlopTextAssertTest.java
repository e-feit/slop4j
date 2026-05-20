package dev.feit.slop4j.assertj;

import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.util.List;
import org.junit.jupiter.api.Test;

class SlopTextAssertTest {

    @Test
    void passesForConcreteTechnicalText() {
        assertThatSlop(
                        """
                        Create a Spring Boot service with one POST /payments endpoint.
                        Persist requests in PostgreSQL 16 using Flyway migrations.
                        Expose p95 latency and error rate via Micrometer.
                        """)
                .hasSlopScoreBelow(40.0)
                .hasActionabilityScoreAbove(0.3)
                .containsConcreteDetails();
    }

    @Test
    void failsWithReportDetailsForHighSlopText() {
        SlopTextAssert assertion =
                assertThatSlop(
                        """
                        We leverage agentic AI to unlock seamless enterprise-grade transformation across modern workflows.
                        Our robust platform enables scalable innovation and future-proof orchestration.
                        The holistic strategy accelerates optimization, modernization, resilience and alignment.
                        """);

        assertThatThrownBy(() -> assertion.hasSlopScoreBelow(40.0))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected slop score to be below 40.0")
                .hasMessageContaining("Report:")
                .hasMessageContaining("verdict:")
                .hasMessageContaining("Findings:");
    }

    @Test
    void recognizesGermanSlopTextWhenGermanIsEnabled() {
        assertThatSlop(
                        """
                        Unsere KI-gestützte Plattform ermöglicht eine nahtlose und skalierbare Transformation moderner Arbeitsabläufe.
                        Sie schafft Synergie, optimiert Innovation und verbessert die Effizienz durch ganzheitliche Orchestrierung.
                        """,
                        Language.GERMAN)
                .hasSlopScoreAbove(40.0);
    }

    @Test
    void cachesActualReport() {
        SlopTextAssert assertion = assertThatSlop("Create POST /payments in PostgreSQL 16.");

        SlopReport first = assertion.actualReport();
        SlopReport second = assertion.actualReport();

        assertThat(second).isSameAs(first);
    }

    @Test
    void delegatesConvenienceAssertionsToReportValues() {
        SlopAnalyzer analyzer =
                text ->
                        new SlopReport(
                                98.0,
                                0.4,
                                0.5,
                                0.1,
                                0.1,
                                0.1,
                                0.0,
                                0.0,
                                SlopVerdict.CERTIFIED_BRAINLESS_SLOP,
                                List.of());

        assertThatSlop("ignored", analyzer)
                .soundsLikeLinkedInPost()
                .isInsufficientlyActionable()
                .isBrainlessSlop()
                .isBoardDeckReady();
    }
}
