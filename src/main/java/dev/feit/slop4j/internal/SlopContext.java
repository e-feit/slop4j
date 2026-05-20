package dev.feit.slop4j.internal;

import dev.feit.slop4j.internal.dictionary.DictionarySet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record SlopContext(
        String originalText,
        String normalizedText,
        List<String> tokens,
        List<String> sentences,
        DictionarySet dictionaries) {

    public SlopContext {
        Objects.requireNonNull(originalText, "originalText");
        Objects.requireNonNull(normalizedText, "normalizedText");
        Objects.requireNonNull(tokens, "tokens");
        Objects.requireNonNull(sentences, "sentences");
        Objects.requireNonNull(dictionaries, "dictionaries");
        tokens = List.copyOf(tokens);
        sentences = List.copyOf(sentences);
    }

    public static SlopContext create(@Nullable String text, DictionarySet dictionaries) {
        String original = text == null ? "" : text;
        String normalized = original.toLowerCase(Locale.ROOT);
        return new SlopContext(
                original,
                normalized,
                TextTokenizer.tokens(original),
                TextTokenizer.sentences(original),
                dictionaries);
    }

    public int tokenCount() {
        return tokens.size();
    }

    public int sentenceCount() {
        return Math.max(1, sentences.size());
    }
}
