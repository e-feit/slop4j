package dev.feit.slop4j.assertj;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import org.assertj.core.api.AbstractAssert;
import org.jspecify.annotations.Nullable;

/** AssertJ assertions for raw text analyzed through a {@link SlopAnalyzer}. */
public final class SlopTextAssert extends AbstractAssert<SlopTextAssert, String> {

    private final SlopAnalyzer analyzer;
    private @Nullable SlopReport report;

    SlopTextAssert(@Nullable String actual, SlopAnalyzer analyzer) {
        super(actual, SlopTextAssert.class);
        this.analyzer = analyzer;
    }

    /**
     * Requires the slop score to be lower than the exclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopTextAssert hasSlopScoreBelow(double maximumExclusive) {
        reportAssert().hasSlopScoreBelow(maximumExclusive);
        return this;
    }

    /**
     * Requires the slop score to be at most the inclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopTextAssert hasSlopScoreAtMost(double maximumInclusive) {
        reportAssert().hasSlopScoreAtMost(maximumInclusive);
        return this;
    }

    /**
     * Requires the slop score to be higher than the exclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopTextAssert hasSlopScoreAbove(double minimumExclusive) {
        reportAssert().hasSlopScoreAbove(minimumExclusive);
        return this;
    }

    /**
     * Requires the slop score to be at least the inclusive threshold in the {@code 0.0..100.0}
     * range.
     */
    public SlopTextAssert hasSlopScoreAtLeast(double minimumInclusive) {
        reportAssert().hasSlopScoreAtLeast(minimumInclusive);
        return this;
    }

    /**
     * Requires buzzword density to be lower than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopTextAssert hasBuzzwordDensityBelow(double maximumExclusive) {
        reportAssert().hasBuzzwordDensityBelow(maximumExclusive);
        return this;
    }

    /**
     * Requires buzzword density to be higher than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopTextAssert hasBuzzwordDensityAbove(double minimumExclusive) {
        reportAssert().hasBuzzwordDensityAbove(minimumExclusive);
        return this;
    }

    /**
     * Requires vague phrase density to be lower than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopTextAssert hasVaguePhraseDensityBelow(double maximumExclusive) {
        reportAssert().hasVaguePhraseDensityBelow(maximumExclusive);
        return this;
    }

    /**
     * Requires the concreteness score to be higher than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopTextAssert hasConcretenessScoreAbove(double minimumExclusive) {
        reportAssert().hasConcretenessScoreAbove(minimumExclusive);
        return this;
    }

    /**
     * Requires the actionability score to be higher than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopTextAssert hasActionabilityScoreAbove(double minimumExclusive) {
        reportAssert().hasActionabilityScoreAbove(minimumExclusive);
        return this;
    }

    /**
     * Requires the evidence score to be higher than the exclusive threshold in the {@code 0.0..1.0}
     * range.
     */
    public SlopTextAssert hasEvidenceScoreAbove(double minimumExclusive) {
        reportAssert().hasEvidenceScoreAbove(minimumExclusive);
        return this;
    }

    /**
     * Requires the repetition score to be lower than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopTextAssert hasRepetitionScoreBelow(double maximumExclusive) {
        reportAssert().hasRepetitionScoreBelow(maximumExclusive);
        return this;
    }

    /**
     * Requires the overconfidence score to be lower than the exclusive threshold in the {@code
     * 0.0..1.0} range.
     */
    public SlopTextAssert hasOverconfidenceScoreBelow(double maximumExclusive) {
        reportAssert().hasOverconfidenceScoreBelow(maximumExclusive);
        return this;
    }

    /** Requires the report verdict to match the expected value exactly. */
    public SlopTextAssert hasVerdict(SlopVerdict expected) {
        reportAssert().hasVerdict(expected);
        return this;
    }

    /** Requires the report verdict not to match the supplied value. */
    public SlopTextAssert doesNotHaveVerdict(SlopVerdict unexpected) {
        reportAssert().doesNotHaveVerdict(unexpected);
        return this;
    }

    /** Requires at least one finding with the supplied type. */
    public SlopTextAssert hasFindingOfType(SlopFindingType expectedType) {
        reportAssert().hasFindingOfType(expectedType);
        return this;
    }

    /** Requires no finding with the supplied type. */
    public SlopTextAssert hasNoFindingOfType(SlopFindingType unexpectedType) {
        reportAssert().hasNoFindingOfType(unexpectedType);
        return this;
    }

    /** Requires at least one finding with the supplied severity. */
    public SlopTextAssert hasFindingWithSeverity(Severity expectedSeverity) {
        reportAssert().hasFindingWithSeverity(expectedSeverity);
        return this;
    }

    /** Requires concreteness or evidence score to be higher than {@code 0.5}. */
    public SlopTextAssert containsConcreteDetails() {
        reportAssert().containsConcreteDetails();
        return this;
    }

    /** Requires concreteness and evidence score to be lower than {@code 0.25}. */
    public SlopTextAssert lacksConcreteDetails() {
        reportAssert().lacksConcreteDetails();
        return this;
    }

    /** Requires actionability score to be higher than {@code 0.5}. */
    public SlopTextAssert isActionable() {
        reportAssert().isActionable();
        return this;
    }

    /** Requires actionability score below {@code 0.25} or a low-actionability finding. */
    public SlopTextAssert isInsufficientlyActionable() {
        reportAssert().isInsufficientlyActionable();
        return this;
    }

    /**
     * Requires the verdict to be neither {@code LINKEDIN_READY} nor {@code BOARD_APPROVED_SLOP}.
     */
    public SlopTextAssert doesNotSoundLikeLinkedInPost() {
        reportAssert().doesNotSoundLikeLinkedInPost();
        return this;
    }

    /** Requires the verdict to be {@code LINKEDIN_READY} or {@code BOARD_APPROVED_SLOP}. */
    public SlopTextAssert soundsLikeLinkedInPost() {
        reportAssert().soundsLikeLinkedInPost();
        return this;
    }

    /** Requires verdict {@code BOARD_APPROVED_SLOP} or slop score at least {@code 85.0}. */
    public SlopTextAssert isBoardDeckReady() {
        reportAssert().isBoardDeckReady();
        return this;
    }

    /** Requires evidence score below {@code 0.2} and concreteness score below {@code 0.3}. */
    public SlopTextAssert containsNoImplementationDetails() {
        reportAssert().containsNoImplementationDetails();
        return this;
    }

    /**
     * Requires slop score at least {@code 75.0}, actionability score below {@code 0.3}, and
     * evidence score below {@code 0.3}.
     */
    public SlopTextAssert maximizesPlausibleDeniability() {
        reportAssert().maximizesPlausibleDeniability();
        return this;
    }

    /** Returns the lazily computed report, reusing the same instance for subsequent calls. */
    public SlopReport actualReport() {
        SlopReport current = report;
        if (current == null) {
            current = analyzer.analyze(actual);
            report = current;
        }
        return current;
    }

    private SlopReportAssert reportAssert() {
        return new SlopReportAssert(actualReport());
    }
}
