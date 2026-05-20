package dev.feit.slop4j.internal.rules;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import java.util.List;
import org.junit.jupiter.api.Test;

class VaguePhraseRuleTest {

    @Test
    void detectsPhrasesAcrossMergedLanguages() {
        SlopRule.Result result =
                new VaguePhraseRule()
                        .analyze(
                                SlopContext.create(
                                        "It depends. In many cases kommt darauf an.",
                                        new ResourceDictionaryLoader()
                                                .load(List.of(Language.ENGLISH, Language.GERMAN))),
                                120);

        assertThat(result.vaguePhraseDensity()).isGreaterThan(0.0);
        assertThat(result.findings())
                .extracting(finding -> finding.type())
                .contains(SlopFindingType.VAGUE_PHRASE);
    }
}
