package dev.feit.slop4j.examples;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;

public final class MultilingualAnalysisExample {

    private static final String SAMPLE_TEXT =
            """
            Unsere KI-gestützte Plattform ermöglicht eine nahtlose und skalierbare Transformation.
            Sie schafft Synergie, optimiert Innovation und verbessert die Effizienz.
            """;

    private final SlopAnalyzer analyzer;

    public MultilingualAnalysisExample() {
        this(SlopAnalyzer.builder().languages(Language.ENGLISH, Language.GERMAN).build());
    }

    MultilingualAnalysisExample(SlopAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public SlopReport analyze(String text) {
        return analyzer.analyze(text);
    }

    public static void main(String[] args) {
        BasicAnalysisExample.printReport(new MultilingualAnalysisExample().analyze(SAMPLE_TEXT));
    }
}
