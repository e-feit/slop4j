package dev.feit.slop4j.internal.rules;

import java.util.regex.Pattern;

final class EvidenceSignals {

	private static final Pattern SUPPORT = Pattern.compile(
			"https?://\\S+|\\[[1-9]\\d*\\]|\\b\\d+(?:[.,]\\d+)?\\s*(?:%|(?:ms|milliseconds?|millisekunden|seconds?|sekunden|minutes?|minuten|hours?|stunden|requests?|anfragen|users?|nutzer|tests?|cases|fälle|samples?|stichproben)\\b)",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private EvidenceSignals() {
	}

	static boolean hasSupport(String sentence) {
		return SUPPORT.matcher(sentence).find();
	}
}
