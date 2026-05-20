package dev.feit.slop4j.internal.rules;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import org.junit.jupiter.api.Test;

class ActionabilityRuleTest {

    @Test
    void recognizesActiveVerbsAndCommands() {
        SlopRule.Result result =
                new ActionabilityRule()
                        .analyze(
                                SlopContext.create(
                                        """
                                        Create the service. Configure Redis. Deploy and validate it.
                                        mvn test
                                        """,
                                        new ResourceDictionaryLoader().load(Language.ENGLISH)),
                                120);

        assertThat(result.actionabilityScore()).isGreaterThan(0.5);
    }

    @Test
    void emitsFindingOnlyForLongLowActionabilityText() {
        String longText = "transformation ".repeat(90);

        SlopRule.Result result =
                new ActionabilityRule()
                        .analyze(
                                SlopContext.create(
                                        longText,
                                        new ResourceDictionaryLoader().load(Language.ENGLISH)),
                                120);

        assertThat(result.findings())
                .extracting(finding -> finding.type())
                .contains(SlopFindingType.LOW_ACTIONABILITY);
    }
}
