package dev.feit.slop4j.cli.internal;

public final class CliException extends RuntimeException {

	private final ExitCode exitCode;

	CliException(String message, ExitCode exitCode) {
		super(message);
		this.exitCode = exitCode;
	}

	public ExitCode exitCode() {
		return exitCode;
	}

	static CliException usage(String message) {
		return new CliException(message, ExitCode.USAGE_ERROR);
	}
}
