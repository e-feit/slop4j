package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThresholdPolicyTest {

	@Test
	void detectsMaximumSlopViolationWhenEnabled() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null, null,
				null, false, 5, 120);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(resultWithScore(72.8));

		assertThat(decision.exceedsMaximumSlop()).isTrue();
		assertThat(decision.belowMinimumSlop()).isFalse();
		assertThat(decision.violatesPolicy()).isTrue();
	}

	@Test
	void ignoresMaximumSlopViolationWhenDisabled() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, false, false, null,
				null, null, false, 5, 120);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(resultWithScore(72.8));

		assertThat(decision.violatesPolicy()).isFalse();
	}

	@Test
	void detectsTooConcreteViolationWhenEnabled() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, false, true, null, null,
				null, false, 5, 120);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(resultWithScore(13.7));

		assertThat(decision.exceedsMaximumSlop()).isFalse();
		assertThat(decision.belowMinimumSlop()).isTrue();
		assertThat(decision.violatesPolicy()).isTrue();
	}

	@Test
	void summarizesViolationCounts() {
		FileSlopResult maximumResult = resultWithScore(90.0);
		FileSlopResult minimumResult = resultWithScore(10.0);
		AuditSummary summary = new AuditSummary(List.of(maximumResult, minimumResult),
				List.of(new AuditDecision(maximumResult, true, false), new AuditDecision(minimumResult, false, true)));

		assertThat(summary.scannedFileCount()).isEqualTo(2);
		assertThat(summary.maximumSlopViolationCount()).isEqualTo(1);
		assertThat(summary.minimumSlopViolationCount()).isEqualTo(1);
		assertThat(summary.hasPolicyViolations()).isTrue();
	}

	private static FileSlopResult resultWithScore(double slopScore) {
		return new FileSlopResult(Path.of("README.md"), new SlopReport(slopScore, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				SlopVerdict.CLEAN, List.<SlopFinding>of()));
	}
}
