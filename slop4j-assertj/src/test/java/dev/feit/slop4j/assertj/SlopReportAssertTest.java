package dev.feit.slop4j.assertj;

import static dev.feit.slop4j.assertj.SlopAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.util.List;
import org.junit.jupiter.api.Test;

class SlopReportAssertTest {

    @Test
    void passesNumericalAssertions() {
        assertThat(report(38.0, 0.1, 0.2, 0.7, 0.8, 0.9, 0.1, 0.2, SlopVerdict.CLEAN))
                .hasSlopScoreBelow(40.0)
                .hasSlopScoreAtMost(38.0)
                .hasSlopScoreAbove(20.0)
                .hasSlopScoreAtLeast(38.0)
                .hasBuzzwordDensityBelow(0.2)
                .hasBuzzwordDensityAbove(0.05)
                .hasVaguePhraseDensityBelow(0.3)
                .hasConcretenessScoreAbove(0.6)
                .hasActionabilityScoreAbove(0.7)
                .hasEvidenceScoreAbove(0.8)
                .hasRepetitionScoreBelow(0.2)
                .hasOverconfidenceScoreBelow(0.3);
    }

    @Test
    void reportsNumericalAssertionFailureWithReportDetails() {
        assertThatThrownBy(
                        () ->
                                assertThat(
                                                report(
                                                        72.8,
                                                        0.18,
                                                        0.5,
                                                        0.1,
                                                        0.12,
                                                        0.0,
                                                        0.0,
                                                        0.25,
                                                        SlopVerdict.LINKEDIN_READY,
                                                        finding(SlopFindingType.LOW_EVIDENCE)))
                                        .hasSlopScoreBelow(40.0))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected slop score to be below 40.0")
                .hasMessageContaining("but was 72.8")
                .hasMessageContaining("verdict: LINKEDIN_READY")
                .hasMessageContaining("LOW_EVIDENCE");
    }

    @Test
    void rejectsInvalidThresholds() {
        SlopReport report = report(10.0, 0.0, 0.0, 0.8, 0.8, 0.8, 0.0, 0.0, SlopVerdict.CLEAN);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> assertThat(report).hasSlopScoreBelow(101.0))
                .withMessageContaining("slop score threshold");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> assertThat(report).hasBuzzwordDensityBelow(Double.NaN))
                .withMessageContaining("buzzword density threshold");
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> assertThat(report).hasEvidenceScoreAbove(Double.POSITIVE_INFINITY))
                .withMessageContaining("evidence score threshold");
    }

    @Test
    void passesVerdictAndFindingAssertions() {
        assertThat(
                        report(
                                85.0,
                                SlopVerdict.BOARD_APPROVED_SLOP,
                                finding(SlopFindingType.OVERCONFIDENCE)))
                .hasVerdict(SlopVerdict.BOARD_APPROVED_SLOP)
                .doesNotHaveVerdict(SlopVerdict.CLEAN)
                .hasFindingOfType(SlopFindingType.OVERCONFIDENCE)
                .hasNoFindingOfType(SlopFindingType.LOW_EVIDENCE)
                .hasFindingWithSeverity(Severity.WARNING);
    }

    @Test
    void rejectsNullVerdictAndFindingArguments() {
        SlopReport report = report(10.0, SlopVerdict.CLEAN);

        assertThatNullPointerException().isThrownBy(() -> assertThat(report).hasVerdict(null));
        assertThatNullPointerException()
                .isThrownBy(() -> assertThat(report).hasFindingOfType(null));
        assertThatNullPointerException()
                .isThrownBy(() -> assertThat(report).hasFindingWithSeverity(null));
    }

    @Test
    void reportsFindingFailureWithAvailableFindings() {
        assertThatThrownBy(
                        () ->
                                assertThat(
                                                report(
                                                        20.0,
                                                        SlopVerdict.CLEAN,
                                                        finding(SlopFindingType.OVERCONFIDENCE)))
                                        .hasNoFindingOfType(SlopFindingType.OVERCONFIDENCE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected no finding of type OVERCONFIDENCE")
                .hasMessageContaining("OVERCONFIDENCE WARNING");
    }

    @Test
    void passesConvenienceAssertions() {
        assertThat(report(88.0, 0.4, 0.6, 0.2, 0.2, 0.1, 0.0, 0.0, SlopVerdict.BOARD_APPROVED_SLOP))
                .lacksConcreteDetails()
                .isInsufficientlyActionable()
                .soundsLikeLinkedInPost()
                .isBoardDeckReady()
                .containsNoImplementationDetails()
                .maximizesPlausibleDeniability();

        assertThat(report(98.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, SlopVerdict.CERTIFIED_BRAINLESS_SLOP))
                .isBrainlessSlop()
                .soundsLikeLinkedInPost()
                .isBoardDeckReady();

        assertThat(report(10.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, SlopVerdict.BRAIN_FREE_ZONE))
                .isBrainFreeZone();

        assertThat(report(10.0, 0.0, 0.0, 0.6, 0.6, 0.6, 0.0, 0.0, SlopVerdict.DANGEROUSLY_USEFUL))
                .containsConcreteDetails()
                .isActionable()
                .doesNotSoundLikeLinkedInPost();
    }

    @Test
    void nullReportFailsAtAssertionTime() {
        assertThatThrownBy(() -> assertThat((SlopReport) null).hasSlopScoreBelow(40.0))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expecting actual not to be null");
    }

    private static SlopReport report(double score, SlopVerdict verdict, SlopFinding... findings) {
        return report(score, 0.0, 0.0, 0.8, 0.8, 0.8, 0.0, 0.0, verdict, findings);
    }

    private static SlopReport report(
            double score,
            double buzzwordDensity,
            double vaguePhraseDensity,
            double concretenessScore,
            double actionabilityScore,
            double evidenceScore,
            double repetitionScore,
            double overconfidenceScore,
            SlopVerdict verdict,
            SlopFinding... findings) {
        return new SlopReport(
                score,
                buzzwordDensity,
                vaguePhraseDensity,
                concretenessScore,
                actionabilityScore,
                evidenceScore,
                repetitionScore,
                overconfidenceScore,
                verdict,
                List.of(findings));
    }

    private static SlopFinding finding(SlopFindingType type) {
        return new SlopFinding(
                type, Severity.WARNING, "Text contains a matching finding.", "evidence");
    }
}
