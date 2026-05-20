package dev.feit.slop4j.internal.dictionary;

import java.util.List;

record LanguageDictionary(List<String> buzzwords, List<String> abstractNouns, List<String> vaguePhrases,
		List<String> weaselWords, List<String> activeVerbs, List<String> concreteTerms, List<String> claimMarkers) {
}
