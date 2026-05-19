package dev.feit.slop4j;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SlopAnalyzerTest {

    @Test
    void analyzesHelloWorldText() {
        SlopReport report = SlopAnalyzer.builder().build().analyze("Hello, world.");

        assertThat(report.slopScore()).isZero();
        assertThat(report.verdict()).isEqualTo(SlopVerdict.DANGEROUSLY_USEFUL);
        assertThat(report.findings()).isEmpty();
    }
}
