package dev.feit.slop4j.internal.dictionary;

import dev.feit.slop4j.Language;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.yaml.snakeyaml.Yaml;

public final class ResourceDictionaryLoader {

    private static final String RESOURCE_DIRECTORY = "/dev/feit/slop4j/languages/";

    private final Yaml yaml;

    public ResourceDictionaryLoader() {
        this(new Yaml());
    }

    ResourceDictionaryLoader(Yaml yaml) {
        this.yaml = Objects.requireNonNull(yaml, "yaml");
    }

    public DictionarySet load(Language language) {
        Objects.requireNonNull(language, "language");
        return load(List.of(language));
    }

    public DictionarySet load(Collection<Language> languages) {
        Objects.requireNonNull(languages, "languages");
        if (languages.isEmpty()) {
            throw new IllegalArgumentException("At least one language must be configured.");
        }

        DictionarySetBuilder builder = new DictionarySetBuilder();
        for (Language language : languages) {
            builder.add(read(language));
        }
        return builder.build();
    }

    private LanguageDictionary read(Language language) {
        String resourcePath = RESOURCE_DIRECTORY + resourceName(language);
        try (InputStream input = ResourceDictionaryLoader.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Dictionary resource not found: " + resourcePath);
            }
            Object loaded = yaml.load(input);
            if (!(loaded instanceof Map<?, ?> map)) {
                throw new IllegalStateException(
                        "Dictionary resource must be a YAML mapping: " + resourcePath);
            }
            return new LanguageDictionary(
                    stringList(map, "buzzwords", resourcePath),
                    stringList(map, "abstractNouns", resourcePath),
                    stringList(map, "vaguePhrases", resourcePath),
                    stringList(map, "weaselWords", resourcePath),
                    stringList(map, "activeVerbs", resourcePath),
                    stringList(map, "concreteTerms", resourcePath),
                    stringList(map, "claimMarkers", resourcePath));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read dictionary resource: " + resourcePath, exception);
        }
    }

    private static String resourceName(Language language) {
        return switch (language) {
            case ENGLISH -> "en.yaml";
            case GERMAN -> "de.yaml";
        };
    }

    private static List<String> stringList(Map<?, ?> map, String key, String resourcePath) {
        Object value = map.get(key);
        if (!(value instanceof List<?> list)) {
            throw new IllegalStateException(
                    "Dictionary field must be a YAML sequence: " + resourcePath + "#" + key);
        }

        List<String> values = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof String string)) {
                throw new IllegalStateException(
                        "Dictionary field must contain only strings: " + resourcePath + "#" + key);
            }
            values.add(string);
        }
        return values;
    }

    private static final class DictionarySetBuilder {

        private final Set<String> buzzwords = new LinkedHashSet<>();
        private final Set<String> abstractNouns = new LinkedHashSet<>();
        private final List<String> vaguePhrases = new ArrayList<>();
        private final Set<String> seenVaguePhrases = new LinkedHashSet<>();
        private final Set<String> weaselWords = new LinkedHashSet<>();
        private final Set<String> activeVerbs = new LinkedHashSet<>();
        private final Set<String> concreteTerms = new LinkedHashSet<>();
        private final Set<String> claimMarkers = new LinkedHashSet<>();

        void add(LanguageDictionary dictionary) {
            addAll(buzzwords, dictionary.buzzwords());
            addAll(abstractNouns, dictionary.abstractNouns());
            addPhrases(dictionary.vaguePhrases());
            addAll(weaselWords, dictionary.weaselWords());
            addAll(activeVerbs, dictionary.activeVerbs());
            addAll(concreteTerms, dictionary.concreteTerms());
            addAll(claimMarkers, dictionary.claimMarkers());
        }

        DictionarySet build() {
            return new DictionarySet(
                    unmodifiableSet(buzzwords),
                    unmodifiableSet(abstractNouns),
                    List.copyOf(vaguePhrases),
                    unmodifiableSet(weaselWords),
                    unmodifiableSet(activeVerbs),
                    unmodifiableSet(concreteTerms),
                    unmodifiableSet(claimMarkers));
        }

        private static void addAll(Set<String> target, Collection<String> values) {
            for (String value : values) {
                normalize(value).ifPresent(target::add);
            }
        }

        private void addPhrases(Collection<String> values) {
            for (String value : values) {
                normalize(value)
                        .ifPresent(
                                normalized -> {
                                    if (seenVaguePhrases.add(normalized)) {
                                        vaguePhrases.add(normalized);
                                    }
                                });
            }
        }

        private static Optional<String> normalize(String value) {
            String normalized = value.strip().toLowerCase(Locale.ROOT);
            return normalized.isEmpty() ? Optional.empty() : Optional.of(normalized);
        }

        private static Set<String> unmodifiableSet(Set<String> values) {
            return Collections.unmodifiableSet(new LinkedHashSet<>(values));
        }
    }
}
