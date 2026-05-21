package dev.feit.slop4j.cli.internal;

import java.util.Objects;

public final class ThresholdPolicy {

	private final AuditConfiguration configuration;

	public ThresholdPolicy(AuditConfiguration configuration) {
		this.configuration = Objects.requireNonNull(configuration, "configuration");
	}

	public AuditDecision evaluate(FileSlopResult result) {
		double score = result.report().slopScore();
		boolean exceedsMaximumSlop = configuration.failOnSlop() && score > configuration.maxSlopScore();
		boolean belowMinimumSlop = configuration.failIfTooConcrete() && score < configuration.minSlopScore();
		return new AuditDecision(result, exceedsMaximumSlop, belowMinimumSlop);
	}
}
