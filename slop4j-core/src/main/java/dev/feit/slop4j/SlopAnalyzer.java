package dev.feit.slop4j;

import org.jspecify.annotations.Nullable;

public interface SlopAnalyzer {

    SlopReport analyze(@Nullable String text);

    static SlopReport analyzeText(String text) {
        return builder().build().analyze(text);
    }

    static SlopAnalyzerBuilder builder() {
        return new SlopAnalyzerBuilder();
    }
}
