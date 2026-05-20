package dev.feit.slop4j.assertj;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import java.util.Collection;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Entry point for AssertJ assertions over slop4j text and reports. */
public final class SlopAssertions {

	private SlopAssertions() {
	}

	/**
	 * Creates assertions for text analyzed with the default core analyzer
	 * configuration.
	 *
	 * @param actual
	 *            text to analyze; null is passed to the core analyzer unchanged
	 * @return assertions for the analyzed text
	 */
	public static SlopTextAssert assertThatSlop(@Nullable String actual) {
		return assertThatSlop(actual, SlopAnalyzer.builder().build());
	}

	/**
	 * Creates assertions for text analyzed with the supplied languages.
	 *
	 * @param actual
	 *            text to analyze; null is passed to the core analyzer unchanged
	 * @param first
	 *            first language to enable
	 * @param rest
	 *            additional languages to enable
	 * @return assertions for the analyzed text
	 */
	public static SlopTextAssert assertThatSlop(@Nullable String actual, Language first, Language... rest) {
		return assertThatSlop(actual, SlopAnalyzer.builder().languages(first, rest).build());
	}

	/**
	 * Creates assertions for text analyzed with the supplied language collection.
	 *
	 * @param actual
	 *            text to analyze; null is passed to the core analyzer unchanged
	 * @param languages
	 *            languages to enable
	 * @return assertions for the analyzed text
	 */
	public static SlopTextAssert assertThatSlop(@Nullable String actual, Collection<Language> languages) {
		return assertThatSlop(actual, SlopAnalyzer.builder().languages(languages).build());
	}

	/**
	 * Creates assertions for text analyzed with the supplied analyzer.
	 *
	 * @param actual
	 *            text to analyze; null is passed to the analyzer unchanged
	 * @param analyzer
	 *            analyzer used to compute the report lazily
	 * @return assertions for the analyzed text
	 */
	public static SlopTextAssert assertThatSlop(@Nullable String actual, SlopAnalyzer analyzer) {
		return new SlopTextAssert(actual, Objects.requireNonNull(analyzer, "analyzer"));
	}

	/**
	 * Creates assertions for an existing report.
	 *
	 * @param actual
	 *            report to inspect
	 * @return assertions for the report
	 */
	public static SlopReportAssert assertThat(@Nullable SlopReport actual) {
		return new SlopReportAssert(actual);
	}
}
