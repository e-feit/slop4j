package dev.feit.slop4j.cli.internal;

public enum ExitCode {
	SUCCESS(0), POLICY_VIOLATION(1), USAGE_ERROR(2), IO_ERROR(3);

	private final int code;

	ExitCode(int code) {
		this.code = code;
	}

	public int code() {
		return code;
	}
}
