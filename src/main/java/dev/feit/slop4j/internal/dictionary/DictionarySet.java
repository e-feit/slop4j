package dev.feit.slop4j.internal.dictionary;

import java.util.List;
import java.util.Set;

public record DictionarySet(
        Set<String> buzzwords,
        Set<String> abstractNouns,
        List<String> vaguePhrases,
        Set<String> weaselWords,
        Set<String> activeVerbs,
        Set<String> concreteTerms,
        Set<String> claimMarkers) {}
