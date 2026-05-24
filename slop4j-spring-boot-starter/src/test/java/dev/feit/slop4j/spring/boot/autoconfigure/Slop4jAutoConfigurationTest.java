package dev.feit.slop4j.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class Slop4jAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(Slop4jAutoConfiguration.class));

	@Test
	void createsDefaultAnalyzer() {
		contextRunner.run(context -> assertThat(context).hasSingleBean(SlopAnalyzer.class));
	}

	@Test
	void usesConfiguredLanguagesAndEvidenceLength() {
		contextRunner.withPropertyValues("slop4j.languages[0]=de", "slop4j.max-finding-evidence-length=8")
				.run(context -> {
					SlopAnalyzer analyzer = context.getBean(SlopAnalyzer.class);
					SlopReport report = analyzer.analyze(
							"Wir liefern agentisch enterprise-grade transformation nahtlos innovation disruptiv.");

					assertThat(report.findings()).isNotEmpty();
					assertThat(report.findings().get(0).evidence()).hasSizeLessThanOrEqualTo(8);
				});
	}

	@Test
	void backsOffWhenAnalyzerBeanExists() {
		SlopAnalyzer userAnalyzer = SlopAnalyzer.builder().languages(Language.GERMAN).build();

		contextRunner.withBean(SlopAnalyzer.class, () -> userAnalyzer).run(context -> {
			assertThat(context).hasSingleBean(SlopAnalyzer.class);
			assertThat(context.getBean(SlopAnalyzer.class)).isSameAs(userAnalyzer);
		});
	}

	@Test
	void ignoresUnsupportedMaxSlopScoreProperty() {
		contextRunner.withPropertyValues("slop4j.max-slop-score=42.5")
				.run(context -> assertThat(context).hasSingleBean(SlopAnalyzer.class));
	}

	@Test
	void doesNotCreatePolicyBean() {
		contextRunner.run(context -> assertThat(context).doesNotHaveBean("slop4jPolicy"));
	}

	@Test
	void failsForUnsupportedLanguage() {
		contextRunner.withPropertyValues("slop4j.languages[0]=fr")
				.run(context -> assertThat(context).hasFailed().getFailure()
						.hasRootCauseInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("Unsupported slop4j language: fr"));
	}

	@Test
	void failsForUnsupportedLanguageWhenUserAnalyzerExists() {
		SlopAnalyzer userAnalyzer = SlopAnalyzer.builder().languages(Language.GERMAN).build();

		contextRunner.withBean(SlopAnalyzer.class, () -> userAnalyzer).withPropertyValues("slop4j.languages[0]=fr")
				.run(context -> assertThat(context).hasFailed().getFailure()
						.hasRootCauseInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("Unsupported slop4j language: fr"));
	}

	@Test
	void failsForNegativeEvidenceLength() {
		contextRunner.withPropertyValues("slop4j.max-finding-evidence-length=-1")
				.run(context -> assertThat(context).hasFailed().getFailure()
						.hasRootCauseInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining("Maximum finding evidence length must not be negative."));
	}
}
