package dev.feit.slop4j.cli.internal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CliParser {

	public CliCommand parse(String[] args) {
		if (args.length == 0 || isHelp(args[0])) {
			return new CliCommand.Help();
		}
		if ("--version".equals(args[0])) {
			return new CliCommand.Version();
		}
		if (!"audit".equals(args[0])) {
			throw CliException.usage("Unsupported command: " + args[0]);
		}
		return parseAudit(Arrays.copyOfRange(args, 1, args.length));
	}

	private CliCommand parseAudit(String[] args) {
		Path baseDirectory = Path.of(".");
		List<String> includes = new ArrayList<>();
		List<String> excludes = new ArrayList<>();
		String languages = "en";
		double maxScore = 60.0;
		double minScore = 80.0;
		boolean failOnSlop = true;
		boolean failIfTooConcrete = false;
		boolean failIfNoFiles = false;
		int maxFindings = 5;
		int maxEvidenceLength = 120;

		for (int index = 0; index < args.length; index++) {
			String arg = args[index];
			switch (arg) {
				case "--help", "-h" -> {
					return new CliCommand.Help();
				}
				case "--lang", "-l" -> languages = requiredValue(args, ++index, arg);
				case "--max-score" -> maxScore = parseDouble(requiredValue(args, ++index, arg), "max-score");
				case "--min-score" -> minScore = parseDouble(requiredValue(args, ++index, arg), "min-score");
				case "--exclude" -> excludes.addAll(splitCsv(requiredValue(args, ++index, arg)));
				case "--base-dir" -> baseDirectory = Path.of(requiredValue(args, ++index, arg));
				case "--fail-on-slop" -> failOnSlop = true;
				case "--no-fail-on-slop" -> failOnSlop = false;
				case "--fail-if-too-concrete" -> failIfTooConcrete = true;
				case "--fail-if-no-files" -> failIfNoFiles = true;
				case "--max-findings" -> maxFindings = parseInt(requiredValue(args, ++index, arg), "max-findings");
				case "--max-evidence-length" ->
					maxEvidenceLength = parseInt(requiredValue(args, ++index, arg), "max-evidence-length");
				default -> {
					if (arg.startsWith("-")) {
						throw CliException.usage("Unsupported option: " + arg);
					}
					includes.add(arg);
				}
			}
		}

		AuditConfiguration configuration = AuditConfiguration
				.create(baseDirectory, includes, excludes.isEmpty() ? null : excludes, maxScore, minScore, failOnSlop,
						failIfTooConcrete, failIfNoFiles, maxFindings, maxEvidenceLength)
				.withLanguages(new LanguageParser().parse(languages));
		return new AuditCommand(configuration);
	}

	private static boolean isHelp(String value) {
		return "--help".equals(value) || "-h".equals(value);
	}

	private static String requiredValue(String[] args, int index, String option) {
		if (index >= args.length) {
			throw CliException.usage("Missing value for option: " + option);
		}
		return args[index];
	}

	private static List<String> splitCsv(String value) {
		return Arrays.stream(value.split(",")).map(String::strip).filter(part -> !part.isBlank()).toList();
	}

	private static double parseDouble(String value, String name) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException exception) {
			throw CliException.usage("Invalid numeric value for " + name + ": " + value);
		}
	}

	private static int parseInt(String value, String name) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException exception) {
			throw CliException.usage("Invalid integer value for " + name + ": " + value);
		}
	}
}
