package dev.feit.slop4j.cli.internal;

import java.util.Objects;

public record AuditCommand(AuditConfiguration configuration) implements CliCommand {

	public AuditCommand {
		Objects.requireNonNull(configuration, "configuration");
	}
}
