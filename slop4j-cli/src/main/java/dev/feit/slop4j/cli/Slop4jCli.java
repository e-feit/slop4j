package dev.feit.slop4j.cli;

import dev.feit.slop4j.cli.internal.AuditCommand;
import dev.feit.slop4j.cli.internal.AuditConfiguration;
import dev.feit.slop4j.cli.internal.AuditDecision;
import dev.feit.slop4j.cli.internal.AuditFileScanner;
import dev.feit.slop4j.cli.internal.AuditSummary;
import dev.feit.slop4j.cli.internal.CliCommand;
import dev.feit.slop4j.cli.internal.CliException;
import dev.feit.slop4j.cli.internal.CliParser;
import dev.feit.slop4j.cli.internal.ConsoleReporter;
import dev.feit.slop4j.cli.internal.ExitCode;
import dev.feit.slop4j.cli.internal.FileSlopResult;
import dev.feit.slop4j.cli.internal.SlopFileAuditor;
import dev.feit.slop4j.cli.internal.ThresholdPolicy;
import dev.feit.slop4j.cli.internal.UsageText;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class Slop4jCli {

	private final PrintWriter out;
	private final PrintWriter err;

	public Slop4jCli(PrintWriter out, PrintWriter err) {
		this.out = Objects.requireNonNull(out, "out");
		this.err = Objects.requireNonNull(err, "err");
	}

	public static void main(String[] args) {
		int exitCode = new Slop4jCli(new PrintWriter(System.out, true), new PrintWriter(System.err, true)).run(args);
		System.exit(exitCode);
	}

	public int run(String[] args) {
		try {
			CliCommand command = new CliParser().parse(args);
			if (command instanceof CliCommand.Help) {
				out.print(UsageText.help());
				out.flush();
				return ExitCode.SUCCESS.code();
			}
			if (command instanceof CliCommand.Version) {
				out.println(UsageText.version());
				out.flush();
				return ExitCode.SUCCESS.code();
			}
			return runAudit((AuditCommand) command);
		} catch (CliException exception) {
			err.println(exception.getMessage());
			err.println("Run 'slop4j --help' for usage.");
			err.flush();
			return exception.exitCode().code();
		} catch (IllegalArgumentException exception) {
			err.println(exception.getMessage());
			err.println("Run 'slop4j --help' for usage.");
			err.flush();
			return ExitCode.USAGE_ERROR.code();
		} catch (IOException exception) {
			err.println("I/O error during slop audit: " + exception.getMessage());
			err.flush();
			return ExitCode.IO_ERROR.code();
		}
	}

	private int runAudit(AuditCommand command) throws IOException {
		AuditConfiguration configuration = command.configuration();
		List<Path> files = new AuditFileScanner(configuration.baseDirectory()).scan(configuration.includes(),
				configuration.excludes());
		ConsoleReporter reporter = new ConsoleReporter(out);
		if (files.isEmpty()) {
			reporter.reportNoFiles(configuration.failIfNoFiles());
			return configuration.failIfNoFiles() ? ExitCode.POLICY_VIOLATION.code() : ExitCode.SUCCESS.code();
		}
		List<FileSlopResult> results = new SlopFileAuditor(configuration).audit(files);
		ThresholdPolicy policy = new ThresholdPolicy(configuration);
		List<AuditDecision> decisions = results.stream().map(policy::evaluate).toList();
		AuditSummary summary = new AuditSummary(results, decisions);
		reporter.reportSummary(summary, configuration);
		return summary.hasPolicyViolations() ? ExitCode.POLICY_VIOLATION.code() : ExitCode.SUCCESS.code();
	}
}
