package dev.feit.slop4j.internal.rules;

import java.util.regex.Pattern;

final class EvidenceSignals {

	private static final Pattern SOURCE = Pattern.compile("https?://\\S+|\\[[1-9]\\d*\\]", Pattern.CASE_INSENSITIVE);
	private static final Pattern MEASUREMENT = Pattern.compile(
			"\\b\\d+(?:[.,]\\d+)?\\s*(?:%|(?:ms|milliseconds?|millisekunden|seconds?|sekunden|minutes?|minuten|hours?|stunden|requests?|anfragen|users?|nutzer|tests?|cases|fälle|samples?|stichproben)\\b)",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern OBSERVED_RESULT = Pattern.compile(
			"\\b(?:measured|observed|recorded|reported|processed|handled|completed|passed|failed|reached|achieved|reduced|increased|decreased|improved|trial|result|results|gemessen|beobachtet|erfasst|berichtet|verarbeitet|bestanden|erreicht|gesenkt|gestiegen|betrug|ergebnis|ergebnisse)\\b",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private EvidenceSignals() {
	}

	static boolean hasSupport(String sentence) {
		return SOURCE.matcher(sentence).find()
				|| (MEASUREMENT.matcher(sentence).find() && OBSERVED_RESULT.matcher(sentence).find());
	}
}
