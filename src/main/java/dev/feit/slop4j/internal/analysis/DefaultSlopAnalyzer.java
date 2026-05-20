package dev.feit.slop4j.internal.analysis;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import dev.feit.slop4j.internal.dictionary.DictionarySet;
import dev.feit.slop4j.internal.dictionary.ResourceDictionaryLoader;
import java.util.List;
import java.util.Objects;

public final class DefaultSlopAnalyzer implements SlopAnalyzer {

    private final List<Language> languages;
    private final int maxFindingEvidenceLength;
    private final DictionarySet dictionarySet;

    public DefaultSlopAnalyzer(List<Language> languages, int maxFindingEvidenceLength) {
        this(languages, maxFindingEvidenceLength, new ResourceDictionaryLoader().load(languages));
    }

    DefaultSlopAnalyzer(
            List<Language> languages, int maxFindingEvidenceLength, DictionarySet dictionarySet) {
        this.languages = List.copyOf(Objects.requireNonNull(languages, "languages"));
        this.maxFindingEvidenceLength = maxFindingEvidenceLength;
        this.dictionarySet = Objects.requireNonNull(dictionarySet, "dictionarySet");
    }

    @Override
    public SlopReport analyze(String text) {
        Objects.requireNonNull(text, "text");
        return new SlopReport(0.0, SlopVerdict.DANGEROUSLY_USEFUL, List.of());
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
