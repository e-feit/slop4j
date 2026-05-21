package dev.feit.slop4j.maven.plugin;

import dev.feit.slop4j.maven.plugin.internal.AuditConfiguration;
import dev.feit.slop4j.maven.plugin.internal.AuditDecision;
import dev.feit.slop4j.maven.plugin.internal.AuditSummary;
import dev.feit.slop4j.maven.plugin.internal.ConsoleReporter;
import dev.feit.slop4j.maven.plugin.internal.FileSlopResult;
import dev.feit.slop4j.maven.plugin.internal.MarkdownFileScanner;
import dev.feit.slop4j.maven.plugin.internal.SlopFileAuditor;
import dev.feit.slop4j.maven.plugin.internal.ThresholdPolicy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Audits project documentation with the deterministic Slop4J analyzer.
 */
@Mojo(name = "audit", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true, requiresProject = true)
public final class SlopAuditMojo extends AbstractMojo {

	/**
	 * Base directory used to resolve include and exclude patterns.
	 */
	@Parameter(defaultValue = "${project.basedir}", readonly = true, required = true)
	private File basedir;

	/**
	 * Maximum allowed slop score when {@code failOnSlop} is enabled.
	 */
	@Parameter(defaultValue = "60.0")
	private double maxSlopScore = 60.0;

	/**
	 * Minimum required slop score when {@code failIfTooConcrete} is enabled.
	 */
	@Parameter(defaultValue = "80.0")
	private double minSlopScore = 80.0;

	/**
	 * Fails the build when any scanned file exceeds {@code maxSlopScore}.
	 */
	@Parameter(property = "failOnSlop", defaultValue = "true")
	private boolean failOnSlop = true;

	/**
	 * Fails the build when any scanned file is below {@code minSlopScore}.
	 */
	@Parameter(defaultValue = "false")
	private boolean failIfTooConcrete;

	/**
	 * Analyzer languages. Supported values are {@code en}, {@code english},
	 * {@code de}, {@code german} and {@code deutsch}.
	 */
	@Parameter
	private List<String> languages;

	/**
	 * Project-relative Maven-style glob patterns to scan.
	 */
	@Parameter
	private List<String> includes;

	/**
	 * Project-relative Maven-style glob patterns ignored after include matching.
	 */
	@Parameter
	private List<String> excludes;

	/**
	 * Skips plugin execution.
	 */
	@Parameter(property = "slop4j.skip", defaultValue = "false")
	private boolean skip;

	/**
	 * Fails the build when no files match the configured include and exclude
	 * patterns.
	 */
	@Parameter(defaultValue = "false")
	private boolean failIfNoFiles;

	/**
	 * Maximum number of findings printed for each file.
	 */
	@Parameter(defaultValue = "5")
	private int maxFindingsPerFile = 5;

	/**
	 * Maximum evidence text length passed to {@code SlopAnalyzer}.
	 */
	@Parameter(defaultValue = "120")
	private int maxFindingEvidenceLength = 120;

	@Override
	public void execute() throws MojoExecutionException {
		ConsoleReporter reporter = new ConsoleReporter(getLog());
		if (skip) {
			reporter.reportSkipped();
			return;
		}
		AuditConfiguration configuration = createConfiguration();
		try {
			List<Path> files = new MarkdownFileScanner(configuration.baseDirectory()).scan(configuration.includes(),
					configuration.excludes());
			if (files.isEmpty()) {
				reporter.reportNoFiles(configuration.failIfNoFiles());
				if (configuration.failIfNoFiles()) {
					throw new MojoExecutionException("No files matched the configured slop audit includes.");
				}
				return;
			}
			List<FileSlopResult> results = new SlopFileAuditor(configuration).audit(files);
			ThresholdPolicy policy = new ThresholdPolicy(configuration);
			List<AuditDecision> decisions = results.stream().map(policy::evaluate).toList();
			AuditSummary summary = new AuditSummary(results, decisions);
			reporter.reportSummary(summary, configuration);
			failOnPolicyViolations(summary, configuration);
		} catch (IOException exception) {
			throw new MojoExecutionException("Failed to run Slop4J audit.", exception);
		}
	}

	private AuditConfiguration createConfiguration() throws MojoExecutionException {
		try {
			return AuditConfiguration.create(basedir, maxSlopScore, minSlopScore, failOnSlop, failIfTooConcrete,
					languages, includes, excludes, failIfNoFiles, maxFindingsPerFile, maxFindingEvidenceLength);
		} catch (IllegalArgumentException exception) {
			throw new MojoExecutionException(exception.getMessage(), exception);
		}
	}

	private static void failOnPolicyViolations(AuditSummary summary, AuditConfiguration configuration)
			throws MojoExecutionException {
		if (summary.maximumSlopViolationCount() > 0) {
			throw new MojoExecutionException("Build failed because " + summary.maximumSlopViolationCount()
					+ " file(s) exceeded maxSlopScore=" + configuration.maxSlopScore() + ".");
		}
		if (summary.minimumSlopViolationCount() > 0) {
			throw new MojoExecutionException("Build failed because " + summary.minimumSlopViolationCount()
					+ " file(s) were below minSlopScore=" + configuration.minSlopScore() + ".");
		}
	}
}
