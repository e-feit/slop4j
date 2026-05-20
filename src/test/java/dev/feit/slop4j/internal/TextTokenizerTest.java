package dev.feit.slop4j.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TextTokenizerTest {

    @Test
    void tokenizesHyphenatedWordsAndUmlauts() {
        SlopContext context =
                SlopContext.create(
                        "Enterprise-grade KI-gestützte Überwachung calls deploy(prod).",
                        dev.feit.slop4j.internal.dictionary.DictionarySet.empty());

        assertThat(context.tokens())
                .contains("enterprise-grade", "ki-gestützte", "überwachung", "deploy", "prod");
    }

    @Test
    void splitsSentencesOnTerminalPunctuation() {
        assertThat(TextTokenizer.sentences("One sentence. Two sentences! Three?"))
                .containsExactly("One sentence.", "Two sentences!", "Three?");
    }
}
