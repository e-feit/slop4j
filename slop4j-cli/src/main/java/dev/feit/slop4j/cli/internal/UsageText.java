package dev.feit.slop4j.cli.internal;

import dev.feit.slop4j.cli.Slop4jCli;

public final class UsageText {

	private UsageText() {
	}

	public static String help() {
		return """
				Usage:
				  slop4j audit [files-or-globs...] [options]
				  slop4j --help
				  slop4j --version

				Options:
				  -l, --lang <codes>          Analyzer languages, for example en,de. Default: en
				      --max-score <score>     Maximum allowed slop score. Default: 60.0
				      --min-score <score>     Minimum required score for --fail-if-too-concrete. Default: 80.0
				      --fail-on-slop          Fail when a file exceeds --max-score. Default: enabled
				      --no-fail-on-slop       Report scores without failing on maximum slop
				      --fail-if-too-concrete  Fail when a file is below --min-score
				      --fail-if-no-files      Fail when no files match
				      --exclude <patterns>    Comma-separated exclude patterns. Repeatable
				      --max-findings <count>  Maximum findings printed per file. Default: 5
				      --max-evidence-length <count>
				                                Maximum evidence length passed to the analyzer. Default: 120
				      --base-dir <path>       Base directory for glob resolution. Default: current directory
				  -h, --help                  Show this help
				      --version               Show version
				""";
	}

	public static String version() {
		String version = Slop4jCli.class.getPackage().getImplementationVersion();
		return "slop4j " + (version == null ? "0.1.0-SNAPSHOT" : version);
	}
}
