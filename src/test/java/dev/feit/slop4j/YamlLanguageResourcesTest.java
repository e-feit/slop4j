package dev.feit.slop4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class YamlLanguageResourcesTest {

    @Test
    void languageDictionariesAreMaintainedAsYamlResources() {
        assertDictionaryResource("en");
        assertDictionaryResource("de");
    }

    private static void assertDictionaryResource(String languageCode) {
        String resourcePath = "/dev/feit/slop4j/languages/" + languageCode + ".yaml";

        try (InputStream input =
                YamlLanguageResourcesTest.class.getResourceAsStream(resourcePath)) {
            assertThat(input).as(resourcePath).isNotNull();

            Object loaded = new Yaml().load(input);

            assertThat(loaded)
                    .isInstanceOfSatisfying(
                            Map.class,
                            dictionary ->
                                    assertThat(dictionary.keySet())
                                            .containsExactlyInAnyOrder(
                                                    "buzzwords",
                                                    "abstractNouns",
                                                    "vaguePhrases",
                                                    "weaselWords",
                                                    "activeVerbs",
                                                    "concreteTerms",
                                                    "claimMarkers"));
        } catch (Exception exception) {
            throw new AssertionError("Unable to load " + resourcePath, exception);
        }
    }
}
