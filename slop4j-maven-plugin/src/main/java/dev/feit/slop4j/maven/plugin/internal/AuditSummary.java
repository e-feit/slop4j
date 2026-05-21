package dev.feit.slop4j.maven.plugin.internal;

import java.util.List;
import java.util.Objects;

public record AuditSummary(List<FileSlopResult> results, List<AuditDecision> decisions) {

	public AuditSummary {
		results = List.copyOf(Objects.requireNonNull(results, "results"));
		decisions = List.copyOf(Objects.requireNonNull(decisions, "decisions"));
	}

	public int scannedFileCount() {
		return results.size();
	}

	public long maximumSlopViolationCount() {
		return decisions.stream().filter(AuditDecision::exceedsMaximumSlop).count();
	}

	public long minimumSlopViolationCount() {
		return decisions.stream().filter(AuditDecision::belowMinimumSlop).count();
	}

	public boolean hasPolicyViolations() {
		return decisions.stream().anyMatch(AuditDecision::violatesPolicy);
	}
}
