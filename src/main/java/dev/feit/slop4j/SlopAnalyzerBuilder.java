package dev.feit.slop4j;

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
        return new DefaultAnalyzer(List.copyOf(languages), maxFindingEvidenceLength);
    }

    private static final class DefaultAnalyzer implements SlopAnalyzer {

        private final List<Language> languages;
        private final int maxFindingEvidenceLength;

        private DefaultAnalyzer(List<Language> languages, int maxFindingEvidenceLength) {
            this.languages = languages;
            this.maxFindingEvidenceLength = maxFindingEvidenceLength;
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
    }
}
