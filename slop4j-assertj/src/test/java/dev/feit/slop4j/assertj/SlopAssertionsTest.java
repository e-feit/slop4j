package dev.feit.slop4j.assertj;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.util.List;
import org.junit.jupiter.api.Test;

class SlopAssertionsTest {

	@Test
	void createsTextAssertForDefaultAnalyzer() {
		assertThat(SlopAssertions.assertThatSlop("Create POST /payments.")).isInstanceOf(SlopTextAssert.class);
	}

	@Test
	void createsTextAssertForConfiguredLanguages() {
		SlopReport report = SlopAssertions.assertThatSlop("Nahtlose skalierbare Transformation.", Language.GERMAN)
				.actualReport();

		assertThat(report.findings()).isNotNull();
	}

	@Test
	void createsTextAssertForLanguageCollection() {
		SlopReport report = SlopAssertions
				.assertThatSlop("Create POST /payments.", List.of(Language.ENGLISH, Language.GERMAN)).actualReport();

		assertThat(report.verdict()).isNotNull();
	}

	@Test
	void rejectsEmptyLanguageCollectionThroughCoreBuilder() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> SlopAssertions.assertThatSlop("text", List.of()).actualReport())
				.withMessageContaining("At least one language");
	}

	@Test
	void usesProvidedAnalyzer() {
		SlopAnalyzer analyzer = text -> report(91.0, SlopVerdict.BOARD_APPROVED_SLOP);

		SlopAssertions.assertThatSlop("ignored", analyzer).hasVerdict(SlopVerdict.BOARD_APPROVED_SLOP);
	}

	@Test
	void createsReportAssert() {
		assertThat(SlopAssertions.assertThat(report(10.0, SlopVerdict.CLEAN))).isInstanceOf(SlopReportAssert.class);
	}

	private static SlopReport report(double score, SlopVerdict verdict) {
		return new SlopReport(score, 0.0, 0.0, 0.8, 0.8, 0.8, 0.0, 0.0, verdict, List.of());
	}
}
