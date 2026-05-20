package dev.feit.slop4j.internal.dictionary;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResourceDictionaryLoaderTest {

    @Test
    void loadsAndNormalizesYamlDictionaryForOneLanguage() {
        DictionarySet dictionarySet = new ResourceDictionaryLoader().load(Language.GERMAN);

        assertThat(dictionarySet.buzzwords()).contains("ki-gestützt");
        assertThat(dictionarySet.abstractNouns()).contains("produktivität");
        assertThat(dictionarySet.vaguePhrases()).contains("kommt darauf an");
        assertThat(dictionarySet.activeVerbs()).contains("löschen");
    }

    @Test
    void mergesYamlDictionariesForMultipleLanguages() {
        DictionarySet dictionarySet =
                new ResourceDictionaryLoader().load(List.of(Language.ENGLISH, Language.GERMAN));

        assertThat(dictionarySet.buzzwords()).contains("agentic", "agentisch");
        assertThat(dictionarySet.concreteTerms()).contains("latency", "latenz");
        assertThat(dictionarySet.claimMarkers()).contains("guaranteed", "garantiert");
    }
}
