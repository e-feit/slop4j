package dev.feit.slop4j.internal.rules;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import org.junit.jupiter.api.Test;

class RepetitionRuleTest {

    @Test
    void detectsRepeatedTrigrams() {
        String text = "strategic platform value ".repeat(12);

        SlopRule.Result result =
                new RepetitionRule()
                        .analyze(
                                SlopContext.create(
                                        text,
                                        new ResourceDictionaryLoader().load(Language.ENGLISH)),
                                120);

        assertThat(result.repetitionScore()).isGreaterThanOrEqualTo(0.08);
        assertThat(result.findings())
                .extracting(finding -> finding.type())
                .contains(SlopFindingType.REPETITION);
    }

    @Test
    void ignoresVeryShortTexts() {
        SlopRule.Result result =
                new RepetitionRule()
                        .analyze(
                                SlopContext.create(
                                        "same same same",
                                        new ResourceDictionaryLoader().load(Language.ENGLISH)),
                                120);

        assertThat(result.repetitionScore()).isZero();
    }
}
