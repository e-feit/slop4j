package dev.feit.slop4j;

import java.util.Objects;

/**
 * A single finding produced during analysis.
 *
 * <p>
 * When no compact evidence is available, {@code evidence} is the empty string.
 */
public record SlopFinding(SlopFindingType type, Severity severity, String message, String evidence) {

	public SlopFinding {
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(severity, "severity");
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(evidence, "evidence");
	}
}
