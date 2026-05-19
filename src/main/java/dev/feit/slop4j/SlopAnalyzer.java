package dev.feit.slop4j;

public interface SlopAnalyzer {

    SlopReport analyze(String text);

    static SlopReport analyzeText(String text) {
        return builder().build().analyze(text);
    }

    static SlopAnalyzerBuilder builder() {
        return new SlopAnalyzerBuilder();
    }
}
