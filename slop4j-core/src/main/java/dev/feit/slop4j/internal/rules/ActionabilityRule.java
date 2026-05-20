package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.SlopContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public final class ActionabilityRule implements SlopRule {

	@Override
	public Result analyze(SlopContext context, int maxFindingEvidenceLength) {
		int activeVerbHits = 0;
		List<String> evidence = new ArrayList<>();
		for (String token : context.tokens()) {
			if (context.dictionaries().activeVerbs().contains(token)) {
				activeVerbHits++;
				evidence.add(token);
			}
		}

		int commandHits = 0;
		Matcher commandMatcher = ConcreteAnchorRule.CLI_COMMAND.matcher(context.originalText());
		while (commandMatcher.find()) {
			commandHits++;
			evidence.add(commandMatcher.group().strip());
		}

		double activeVerbScore = Math.min(1.0, activeVerbHits / 6.0);
		double commandScore = Math.min(1.0, commandHits / 3.0);
		double actionabilityScore = 0.7 * activeVerbScore + 0.3 * commandScore;

		List<SlopFinding> findings = new ArrayList<>();
		if (context.tokenCount() >= 80 && actionabilityScore < 0.2) {
			findings.add(new SlopFinding(SlopFindingType.LOW_ACTIONABILITY, Severity.WARNING,
					"Text sounds strategic but provides few executable actions.",
					FindingEvidence.joinDistinct(evidence, maxFindingEvidenceLength)));
		}

		return new Result(0.0, 0.0, 0.0, 0.0, actionabilityScore, 0.0, 0.0, 0.0, findings);
	}
}
