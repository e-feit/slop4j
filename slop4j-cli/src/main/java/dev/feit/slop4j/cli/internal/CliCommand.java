package dev.feit.slop4j.cli.internal;

public sealed interface CliCommand permits CliCommand.Help, CliCommand.Version, AuditCommand {

	record Help() implements CliCommand {
	}

	record Version() implements CliCommand {
	}
}
