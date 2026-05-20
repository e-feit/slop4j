package dev.feit.slop4j.internal.analysis;

import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import dev.feit.slop4j.internal.ScoreMath;
import dev.feit.slop4j.internal.SlopContext;
import dev.feit.slop4j.internal.rules.SlopRule;
import java.util.ArrayList;
import java.util.List;

final class DefaultSlopScorer {

	SlopReport score(SlopContext context, List<SlopRule.Result> results) {
		double buzzwordDensity = 0.0;
		double vaguePhraseDensity = 0.0;
		double abstractNounDensity = 0.0;
		double concretenessScore = 0.0;
		double actionabilityScore = 0.0;
		double evidenceScore = 0.0;
		double repetitionScore = 0.0;
		double overconfidenceScore = 0.0;
		List<SlopFinding> findings = new ArrayList<>();

		for (SlopRule.Result result : results) {
			buzzwordDensity = Math.max(buzzwordDensity, result.buzzwordDensity());
			vaguePhraseDensity = Math.max(vaguePhraseDensity, result.vaguePhraseDensity());
			abstractNounDensity = Math.max(abstractNounDensity, result.abstractNounDensity());
			concretenessScore = Math.max(concretenessScore, result.concretenessScore());
			actionabilityScore = Math.max(actionabilityScore, result.actionabilityScore());
			evidenceScore = Math.max(evidenceScore, result.evidenceScore());
			repetitionScore = Math.max(repetitionScore, result.repetitionScore());
			overconfidenceScore = Math.max(overconfidenceScore, result.overconfidenceScore());
			findings.addAll(result.findings());
		}

		double buzzwordScore = ScoreMath.saturating(buzzwordDensity, 0.08);
		double vagueScore = ScoreMath.saturating(vaguePhraseDensity, 1.5);
		double abstractScore = ScoreMath.saturating(abstractNounDensity, 0.07);
		double lengthFactor = ScoreMath.clamp01(context.tokenCount() / 80.0);
		double adjustedOverconfidence = overconfidenceScore * (1.0 - evidenceScore);

		double rawSlop = 0.24 * buzzwordScore + 0.18 * vagueScore + 0.14 * abstractScore + 0.10 * repetitionScore
				+ 0.12 * adjustedOverconfidence + lengthFactor * (0.08 * (1.0 - evidenceScore)
						+ 0.07 * (1.0 - actionabilityScore) + 0.07 * (1.0 - concretenessScore));

		double slopScore = ScoreMath.round1(Math.min(1.0, rawSlop * 1.5) * 100.0);
		return new SlopReport(slopScore, buzzwordDensity, vaguePhraseDensity, concretenessScore, actionabilityScore,
				evidenceScore, repetitionScore, overconfidenceScore,
				verdict(slopScore, concretenessScore, actionabilityScore, overconfidenceScore, evidenceScore),
				findings);
	}

	private static SlopVerdict verdict(double slopScore, double concretenessScore, double actionabilityScore,
			double overconfidenceScore, double evidenceScore) {
		if (overconfidenceScore > 0.85 && evidenceScore < 0.1) {
			return SlopVerdict.BRAIN_FREE_ZONE;
		}
		if (slopScore < 25.0 && concretenessScore > 0.7 && actionabilityScore > 0.5) {
			return SlopVerdict.DANGEROUSLY_USEFUL;
		}
		if (slopScore < 15.0) {
			return SlopVerdict.CLEAN;
		}
		if (slopScore < 30.0) {
			return SlopVerdict.ACCEPTABLY_FLUFFY;
		}
		if (slopScore < 45.0) {
			return SlopVerdict.SLOP_ADJACENT;
		}
		if (slopScore < 60.0) {
			return SlopVerdict.TOTAL_CORPORATE_NOTHINGNESS;
		}
		if (slopScore < 75.0) {
			return SlopVerdict.LINKEDIN_READY;
		}
		if (slopScore < 85.0) {
			return SlopVerdict.PREMIUM_POLISHED_GARBAGE;
		}
		if (slopScore < 92.0) {
			return SlopVerdict.BOARD_APPROVED_SLOP;
		}
		if (slopScore < 97.0) {
			return SlopVerdict.GARBAGE_IN_SLOP_OUT;
		}
		return SlopVerdict.CERTIFIED_BRAINLESS_SLOP;
	}
}
