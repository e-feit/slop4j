package dev.feit.slop4j;

import dev.feit.slop4j.internal.analysis.DefaultSlopAnalyzer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class SlopAnalyzerBuilder {

    private final List<Language> languages = new ArrayList<>(List.of(Language.ENGLISH));
    private int maxFindingEvidenceLength = 120;

    public SlopAnalyzerBuilder language(Language language) {
        Objects.requireNonNull(language, "language");
        languages.clear();
        languages.add(language);
        return this;
    }

    public SlopAnalyzerBuilder languages(Language first, Language... rest) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(rest, "rest");
        languages.clear();
        languages.add(first);
        for (Language language : rest) {
            languages.add(Objects.requireNonNull(language, "language"));
        }
        return this;
    }

    public SlopAnalyzerBuilder languages(Collection<Language> languages) {
        Objects.requireNonNull(languages, "languages");
        if (languages.isEmpty()) {
            throw new IllegalArgumentException("At least one language must be configured.");
        }
        this.languages.clear();
        for (Language language : languages) {
            this.languages.add(Objects.requireNonNull(language, "language"));
        }
        return this;
    }

    public SlopAnalyzerBuilder maxFindingEvidenceLength(int maxFindingEvidenceLength) {
        if (maxFindingEvidenceLength < 0) {
            throw new IllegalArgumentException(
                    "Maximum finding evidence length must not be negative.");
        }
        this.maxFindingEvidenceLength = maxFindingEvidenceLength;
        return this;
    }

    public SlopAnalyzer build() {
        return new DefaultSlopAnalyzer(List.copyOf(languages), maxFindingEvidenceLength);
    }
}
