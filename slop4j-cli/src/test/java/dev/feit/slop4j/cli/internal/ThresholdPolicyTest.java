package dev.feit.slop4j.cli.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThresholdPolicyTest {

	@Test
	void flagsMaximumSlopViolationOnlyWhenEnabled() {
		AuditConfiguration configuration = AuditConfiguration.create(Path.of("."), List.of("README.md"), null, 60.0,
				80.0, true, false, false, 5, 120);
		FileSlopResult result = resultWithScore(72.8);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(result);

		assertThat(decision.exceedsMaximumSlop()).isTrue();
		assertThat(decision.belowMinimumSlop()).isFalse();
		assertThat(decision.violatesPolicy()).isTrue();
	}

	@Test
	void flagsMinimumSlopViolationOnlyWhenEnabled() {
		AuditConfiguration configuration = AuditConfiguration.create(Path.of("."), List.of("README.md"), null, 60.0,
				80.0, false, true, false, 5, 120);
		FileSlopResult result = resultWithScore(13.7);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(result);

		assertThat(decision.exceedsMaximumSlop()).isFalse();
		assertThat(decision.belowMinimumSlop()).isTrue();
		assertThat(decision.violatesPolicy()).isTrue();
	}

	private static FileSlopResult resultWithScore(double score) {
		SlopReport report = new SlopReport(score, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, SlopVerdict.CLEAN, List.of());
		return new FileSlopResult(Path.of("README.md"), report);
	}
}
