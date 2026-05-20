package dev.feit.slop4j.examples.assertj;

import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

import dev.feit.slop4j.Language;
import org.junit.jupiter.api.Test;

class AssertJQualityGateExampleTest {

    @Test
    void validatesTechnicalDocumentationWithAssertJAssertions() {
        assertThatSlop(AssertJQualityGateExample.technicalDocumentation(), Language.ENGLISH)
                .hasSlopScoreBelow(40.0)
                .hasActionabilityScoreAbove(0.3)
                .containsConcreteDetails()
                .doesNotSoundLikeLinkedInPost();
    }

    @Test
    void validatesBoardDeckNarrativeWithDeterministicAliases() {
        assertThatSlop(AssertJQualityGateExample.strategyDeckNarrative(), Language.ENGLISH)
                .isBoardDeckReady()
                .containsNoImplementationDetails()
                .maximizesPlausibleDeniability();
    }
}
