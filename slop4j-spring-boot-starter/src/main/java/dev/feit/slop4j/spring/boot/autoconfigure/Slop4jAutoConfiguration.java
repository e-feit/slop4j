package dev.feit.slop4j.spring.boot.autoconfigure;

import dev.feit.slop4j.SlopAnalyzer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(SlopAnalyzer.class)
@EnableConfigurationProperties(Slop4jProperties.class)
public class Slop4jAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public SlopAnalyzer slopAnalyzer(Slop4jProperties properties) {
		return SlopAnalyzer.builder().languages(properties.parsedLanguages())
				.maxFindingEvidenceLength(properties.getMaxFindingEvidenceLength()).build();
	}

	@Bean
	Slop4jLanguageValidation slop4jLanguageValidation(Slop4jProperties properties) {
		properties.parsedLanguages();
		return new Slop4jLanguageValidation();
	}
}

final class Slop4jLanguageValidation {
}
