package dev.feit.slop4j.assertj;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import org.assertj.core.api.AbstractAssert;
import org.jspecify.annotations.Nullable;

/** AssertJ assertions for existing {@link SlopReport} instances. */
public final class SlopReportAssert extends AbstractAssert<SlopReportAssert, SlopReport> {

    SlopReportAssert(@Nullable SlopReport actual) {
        super(actual, SlopReportAssert.class);
    }

    /**
     * Requires the slop score to be lower than the exclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopReportAssert hasSlopScoreBelow(double maximumExclusive) {
        assertNumeric(
                "slop score",
                SlopReport::slopScore,
                maximumExclusive,
                100.0,
                "below",
                actualValue -> actualValue < maximumExclusive);
        return this;
    }

    /**
     * Requires the slop score to be at most the inclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopReportAssert hasSlopScoreAtMost(double maximumInclusive) {
        assertNumeric(
                "slop score",
                SlopReport::slopScore,
                maximumInclusive,
                100.0,
                "at most",
                actualValue -> actualValue <= maximumInclusive);
        return this;
    }

    /**
     * Requires the slop score to be higher than the exclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopReportAssert hasSlopScoreAbove(double minimumExclusive) {
        assertNumeric(
                "slop score",
                SlopReport::slopScore,
                minimumExclusive,
                100.0,
                "above",
                actualValue -> actualValue > minimumExclusive);
        return this;
    }

    /**
     * Requires the slop score to be at least the inclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopReportAssert hasSlopScoreAtLeast(double minimumInclusive) {
        assertNumeric(
                "slop score",
                SlopReport::slopScore,
                minimumInclusive,
                100.0,
                "at least",
                actualValue -> actualValue >= minimumInclusive);
        return this;
    }

    /**
     * Requires buzzword density to be lower than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopReportAssert hasBuzzwordDensityBelow(double maximumExclusive) {
        assertUnitScore(
                "buzzword density",
                SlopReport::buzzwordDensity,
                maximumExclusive,
                "below",
                actualValue -> actualValue < maximumExclusive);
        return this;
    }

    /**
     * Requires buzzword density to be higher than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopReportAssert hasBuzzwordDensityAbove(double minimumExclusive) {
        assertUnitScore(
                "buzzword density",
                SlopReport::buzzwordDensity,
                minimumExclusive,
                "above",
                actualValue -> actualValue > minimumExclusive);
        return this;
    }

    /**
     * Requires vague phrase density to be lower than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopReportAssert hasVaguePhraseDensityBelow(double maximumExclusive) {
        assertUnitScore(
                "vague phrase density",
                SlopReport::vaguePhraseDensity,
                maximumExclusive,
                "below",
                actualValue -> actualValue < maximumExclusive);
        return this;
    }

    /**
     * Requires concreteness score to be higher than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopReportAssert hasConcretenessScoreAbove(double minimumExclusive) {
        assertUnitScore(
                "concreteness score",
                SlopReport::concretenessScore,
                minimumExclusive,
                "above",
                actualValue -> actualValue > minimumExclusive);
        return this;
    }

    /**
     * Requires actionability score to be higher than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopReportAssert hasActionabilityScoreAbove(double minimumExclusive) {
        assertUnitScore(
                "actionability score",
                SlopReport::actionabilityScore,
                minimumExclusive,
                "above",
                actualValue -> actualValue > minimumExclusive);
        return this;
    }

    /**
     * Requires evidence score to be higher than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopReportAssert hasEvidenceScoreAbove(double minimumExclusive) {
        assertUnitScore(
                "evidence score",
                SlopReport::evidenceScore,
                minimumExclusive,
                "above",
                actualValue -> actualValue > minimumExclusive);
        return this;
    }

    /**
     * Requires repetition score to be lower than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopReportAssert hasRepetitionScoreBelow(double maximumExclusive) {
        assertUnitScore(
                "repetition score",
                SlopReport::repetitionScore,
                maximumExclusive,
                "below",
                actualValue -> actualValue < maximumExclusive);
        return this;
    }

    /**
     * Requires overconfidence score to be lower than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopReportAssert hasOverconfidenceScoreBelow(double maximumExclusive) {
        assertUnitScore(
                "overconfidence score",
                SlopReport::overconfidenceScore,
                maximumExclusive,
                "below",
                actualValue -> actualValue < maximumExclusive);
        return this;
    }

    /** Requires the report verdict to match the expected value exactly. */
    public SlopReportAssert hasVerdict(SlopVerdict expected) {
        Objects.requireNonNull(expected, "expected");
        SlopReport report = requireReport();
        if (report.verdict() != expected) {
            failWithMessage(
                    "Expected verdict to be %s, but was %s.%n%n%s",
                    expected, report.verdict(), formatReport(report));
        }
        return this;
    }

    /** Requires the report verdict not to match the supplied value. */
    public SlopReportAssert doesNotHaveVerdict(SlopVerdict unexpected) {
        Objects.requireNonNull(unexpected, "unexpected");
        SlopReport report = requireReport();
        if (report.verdict() == unexpected) {
            failWithMessage(
                    "Expected verdict not to be %s, but was %s.%n%n%s",
                    unexpected, report.verdict(), formatReport(report));
        }
        return this;
    }

    /** Requires at least one finding with the supplied type. */
    public SlopReportAssert hasFindingOfType(SlopFindingType expectedType) {
        Objects.requireNonNull(expectedType, "expectedType");
        SlopReport report = requireReport();
        if (report.findings().stream().noneMatch(finding -> finding.type() == expectedType)) {
            failWithMessage(
                    "Expected finding of type %s, but available findings were:%n%s",
                    expectedType, formatFindings(report));
        }
        return this;
    }

    /** Requires no finding with the supplied type. */
    public SlopReportAssert hasNoFindingOfType(SlopFindingType unexpectedType) {
        Objects.requireNonNull(unexpectedType, "unexpectedType");
        SlopReport report = requireReport();
        var matches =
                report.findings().stream()
                        .filter(finding -> finding.type() == unexpectedType)
                        .toList();
        if (!matches.isEmpty()) {
            failWithMessage(
                    "Expected no finding of type %s, but found:%n%s",
                    unexpectedType, formatFindings(matches));
        }
        return this;
    }

    /** Requires at least one finding with the supplied severity. */
    public SlopReportAssert hasFindingWithSeverity(Severity expectedSeverity) {
        Objects.requireNonNull(expectedSeverity, "expectedSeverity");
        SlopReport report = requireReport();
        if (report.findings().stream()
                .noneMatch(finding -> finding.severity() == expectedSeverity)) {
            failWithMessage(
                    "Expected finding with severity %s, but available findings were:%n%s",
                    expectedSeverity, formatFindings(report));
        }
        return this;
    }

    /** Requires concreteness score above {@code 0.5} or evidence score above {@code 0.5}. */
    public SlopReportAssert containsConcreteDetails() {
        SlopReport report = requireReport();
        if (!(report.concretenessScore() > 0.5 || report.evidenceScore() > 0.5)) {
            failWithMessage(
                    "Expected concrete details through concreteness score above 0.5 or evidence score above 0.5.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    /** Requires concreteness and evidence score below {@code 0.25}. */
    public SlopReportAssert lacksConcreteDetails() {
        SlopReport report = requireReport();
        if (!(report.concretenessScore() < 0.25 && report.evidenceScore() < 0.25)) {
            failWithMessage(
                    "Expected concreteness score and evidence score to be below 0.25.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    /** Requires actionability score above {@code 0.5}. */
    public SlopReportAssert isActionable() {
        SlopReport report = requireReport();
        if (report.actionabilityScore() <= 0.5) {
            failWithMessage(
                    "Expected actionability score to be above 0.5.%n%n%s", formatReport(report));
        }
        return this;
    }

    /** Requires actionability score below {@code 0.25} or a low-actionability finding. */
    public SlopReportAssert isInsufficientlyActionable() {
        SlopReport report = requireReport();
        boolean hasLowActionabilityFinding =
                report.findings().stream()
                        .anyMatch(finding -> finding.type() == SlopFindingType.LOW_ACTIONABILITY);
        if (!(report.actionabilityScore() < 0.25 || hasLowActionabilityFinding)) {
            failWithMessage(
                    "Expected actionability score below 0.25 or a LOW_ACTIONABILITY finding.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    /** Requires verdict neither {@code LINKEDIN_READY} nor {@code BOARD_APPROVED_SLOP}. */
    public SlopReportAssert doesNotSoundLikeLinkedInPost() {
        SlopReport report = requireReport();
        if (report.verdict() == SlopVerdict.LINKEDIN_READY
                || report.verdict() == SlopVerdict.BOARD_APPROVED_SLOP) {
            failWithMessage(
                    "Expected verdict not to be LINKEDIN_READY or BOARD_APPROVED_SLOP.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    /** Requires verdict {@code LINKEDIN_READY} or {@code BOARD_APPROVED_SLOP}. */
    public SlopReportAssert soundsLikeLinkedInPost() {
        SlopReport report = requireReport();
        if (!(report.verdict() == SlopVerdict.LINKEDIN_READY
                || report.verdict() == SlopVerdict.BOARD_APPROVED_SLOP)) {
            failWithMessage(
                    "Expected verdict to be LINKEDIN_READY or BOARD_APPROVED_SLOP.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    /** Requires verdict {@code BOARD_APPROVED_SLOP} or slop score at least {@code 85.0}. */
    public SlopReportAssert isBoardDeckReady() {
        SlopReport report = requireReport();
        if (!(report.verdict() == SlopVerdict.BOARD_APPROVED_SLOP || report.slopScore() >= 85.0)) {
            failWithMessage(
                    "Expected verdict BOARD_APPROVED_SLOP or slop score at least 85.0.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    /** Requires evidence score below {@code 0.2} and concreteness score below {@code 0.3}. */
    public SlopReportAssert containsNoImplementationDetails() {
        SlopReport report = requireReport();
        if (!(report.evidenceScore() < 0.2 && report.concretenessScore() < 0.3)) {
            failWithMessage(
                    "Expected evidence score below 0.2 and concreteness score below 0.3.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    /**
     * Requires slop score at least {@code 75.0}, actionability score below {@code 0.3}, and
     * evidence score below {@code 0.3}.
     */
    public SlopReportAssert maximizesPlausibleDeniability() {
        SlopReport report = requireReport();
        if (!(report.slopScore() >= 75.0
                && report.actionabilityScore() < 0.3
                && report.evidenceScore() < 0.3)) {
            failWithMessage(
                    "Expected slop score at least 75.0, actionability score below 0.3, and evidence score below 0.3.%n%n%s",
                    formatReport(report));
        }
        return this;
    }

    private void assertUnitScore(
            String label,
            ToDoubleFunction<SlopReport> extractor,
            double threshold,
            String relation,
            DoubleCondition condition) {
        assertNumeric(label, extractor, threshold, 1.0, relation, condition);
    }

    private void assertNumeric(
            String label,
            ToDoubleFunction<SlopReport> extractor,
            double threshold,
            double maximum,
            String relation,
            DoubleCondition condition) {
        validateThreshold(label, threshold, maximum);
        SlopReport report = requireReport();
        double actualValue = extractor.applyAsDouble(report);
        if (!condition.matches(actualValue)) {
            failWithMessage(
                    "Expected %s to be %s %s, but was %s.%n%n%s",
                    label, relation, threshold, actualValue, formatReport(report));
        }
    }

    private static void validateThreshold(String label, double threshold, double maximum) {
        if (!Double.isFinite(threshold) || threshold < 0.0 || threshold > maximum) {
            throw new IllegalArgumentException(
                    label + " threshold must be finite and within 0.0.." + maximum + ".");
        }
    }

    private SlopReport requireReport() {
        isNotNull();
        return actual;
    }

    private static String formatReport(SlopReport report) {
        return """
                Report:
                  verdict: %s
                  slopScore: %s
                  buzzwordDensity: %s
                  vaguePhraseDensity: %s
                  concretenessScore: %s
                  actionabilityScore: %s
                  evidenceScore: %s
                  repetitionScore: %s
                  overconfidenceScore: %s

                Findings:
                %s"""
                .formatted(
                        report.verdict(),
                        report.slopScore(),
                        report.buzzwordDensity(),
                        report.vaguePhraseDensity(),
                        report.concretenessScore(),
                        report.actionabilityScore(),
                        report.evidenceScore(),
                        report.repetitionScore(),
                        report.overconfidenceScore(),
                        formatFindings(report));
    }

    private static String formatFindings(SlopReport report) {
        return formatFindings(report.findings());
    }

    private static String formatFindings(Iterable<SlopFinding> findings) {
        StringBuilder builder = new StringBuilder();
        for (SlopFinding finding : findings) {
            if (!builder.isEmpty()) {
                builder.append(System.lineSeparator());
            }
            builder.append("  - ")
                    .append(finding.type())
                    .append(' ')
                    .append(finding.severity())
                    .append(": ")
                    .append(finding.message());
            if (!finding.evidence().isBlank()) {
                builder.append(" Evidence: ").append(finding.evidence());
            }
        }
        if (builder.isEmpty()) {
            return "  (none)";
        }
        return builder.toString();
    }

    @FunctionalInterface
    private interface DoubleCondition {
        boolean matches(double actualValue);
    }
}
