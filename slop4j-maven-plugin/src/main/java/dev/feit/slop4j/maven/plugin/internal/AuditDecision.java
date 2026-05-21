package dev.feit.slop4j.maven.plugin.internal;

import java.util.Objects;

public record AuditDecision(FileSlopResult result, boolean exceedsMaximumSlop, boolean belowMinimumSlop) {

	public AuditDecision {
		Objects.requireNonNull(result, "result");
	}

	public boolean violatesPolicy() {
		return exceedsMaximumSlop || belowMinimumSlop;
	}

	public String displayPath() {
		return result.displayPath();
	}
}
