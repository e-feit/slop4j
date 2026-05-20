package dev.feit.slop4j.internal.analysis;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.dictionary.DictionarySet;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import dev.feit.slop4j.internal.rules.AbstractNounRule;
import dev.feit.slop4j.internal.rules.ActionabilityRule;
import dev.feit.slop4j.internal.rules.BuzzwordRule;
import dev.feit.slop4j.internal.rules.ConcreteAnchorRule;
import dev.feit.slop4j.internal.rules.OverconfidenceRule;
import dev.feit.slop4j.internal.rules.RepetitionRule;
import dev.feit.slop4j.internal.rules.SlopRule;
import dev.feit.slop4j.internal.rules.VaguePhraseRule;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class DefaultSlopAnalyzer implements SlopAnalyzer {

    private final List<Language> languages;
    private final int maxFindingEvidenceLength;
    private final DictionarySet dictionarySet;
    private final List<SlopRule> rules;
    private final DefaultSlopScorer scorer = new DefaultSlopScorer();

    public DefaultSlopAnalyzer(List<Language> languages, int maxFindingEvidenceLength) {
        this(languages, maxFindingEvidenceLength, new ResourceDictionaryLoader().load(languages));
    }

    DefaultSlopAnalyzer(
            List<Language> languages, int maxFindingEvidenceLength, DictionarySet dictionarySet) {
        this.languages = List.copyOf(Objects.requireNonNull(languages, "languages"));
        this.maxFindingEvidenceLength = maxFindingEvidenceLength;
        this.dictionarySet = Objects.requireNonNull(dictionarySet, "dictionarySet");
        this.rules =
                List.of(
                        new BuzzwordRule(),
                        new VaguePhraseRule(),
                        new AbstractNounRule(),
                        new ConcreteAnchorRule(),
                        new ActionabilityRule(),
                        new RepetitionRule(),
                        new OverconfidenceRule());
    }

    @Override
    public SlopReport analyze(@Nullable String text) {
        SlopContext context = SlopContext.create(text, dictionarySet);
        if (context.tokenCount() == 0) {
            return new SlopReport(
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, SlopVerdict.CLEAN, List.of());
        }

        List<SlopRule.Result> results = new ArrayList<>();
        for (SlopRule rule : rules) {
            results.add(rule.analyze(context, maxFindingEvidenceLength));
        }
        return scorer.score(context, results);
    }

    List<Language> languages() {
        return languages;
    }

    int maxFindingEvidenceLength() {
        return maxFindingEvidenceLength;
    }

    DictionarySet dictionarySet() {
        return dictionarySet;
    }
}
