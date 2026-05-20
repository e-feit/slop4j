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
        Set<String> claimMarkers) {

    public static DictionarySet empty() {
        return new DictionarySet(
                Set.of(), Set.of(), List.of(), Set.of(), Set.of(), Set.of(), Set.of());
    }

    public DictionarySet {
        buzzwords = Set.copyOf(buzzwords);
        abstractNouns = Set.copyOf(abstractNouns);
        vaguePhrases = List.copyOf(vaguePhrases);
        weaselWords = Set.copyOf(weaselWords);
        activeVerbs = Set.copyOf(activeVerbs);
        concreteTerms = Set.copyOf(concreteTerms);
        claimMarkers = Set.copyOf(claimMarkers);
    }
}
