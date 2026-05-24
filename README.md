# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

<p align="center">
  <img src="img/slop4j.png" alt="slop4j logo" width="240">
</p>

Enterprise-grade AI Output Governance and Deterministic Slop Mitigation for JVM-based Transformation Narratives.

## Overview

`slop4j` provides a robust, deterministic framework for auditing AI-generated content. It identifies high concentrations of vague phrasing, unearned confidence, and insufficient actionability—common indicators of low-cognitive-involvement narratives. 

By utilizing local rule-based heuristics and validated language dictionaries, `slop4j` enables organizations to safeguard their strategic alignment without the need for external API calls or non-deterministic "AI-on-AI" analysis.

It focuses exclusively on "textual smells"—markers of generic phrasing that reduce the strategic utility of documentation, ADRs, and transformation roadmaps.

## Requirements

- Java 17 or higher

## Installation

<!-- slop4j-installation:start -->
Latest version: `0.2.1`.

### Core Library

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-core</artifactId>
    <version>0.2.1</version>
</dependency>
```

### AssertJ Assertions

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-assertj</artifactId>
    <version>0.2.1</version>
    <scope>test</scope>
</dependency>
```

### Maven Plugin

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.2.1</version>
</plugin>
```

### Spring Boot Starter

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-spring-boot-starter</artifactId>
    <version>0.2.1</version>
</dependency>
```
<!-- slop4j-installation:end -->

The Java package and automatic module name are `dev.feit.slop4j`.

## Quick Start

```java
import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;

SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .build();

SlopReport report = analyzer.analyze("""
    We leverage agentic AI to unlock seamless enterprise-grade transformation.
    """);

System.out.println(report.slopScore());
System.out.println(report.verdict());
System.out.println(report.findings());
```

For the input shown above, the output provides deterministic governance signals:

```text
56.4
TOTAL_CORPORATE_NOTHINGNESS
[SlopFinding[type=BUZZWORD_DENSITY, severity=WARNING, message=Buzzword density is suspiciously high., evidence=leverage, agentic, unlock, seamless, enterprise-grade, transformation]]
```

## AssertJ Assertions

The `slop4j-assertj` module provides high-level assertions to enforce narrative quality standards in automated tests.

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails();
```

For advanced governance, use specific strategic alignment conditions:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady() // High slop, zero implementation details
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

## Maven Plugin

The `slop4j-maven-plugin` audits documentation files during the build process to prevent "dangerously concrete" content from entering the codebase.

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.2.1</version><!-- slop4j-release-version -->
    <configuration>
        <maxSlopScore>60.0</maxSlopScore>
        <languages>
            <language>en</language>
            <language>de</language>
        </languages>
        <includes>
            <include>README.md</include>
            <include>README_DE.md</include>
            <include>docs/**/*.md</include>
            <include>adr/**/*.md</include>
        </includes>
        <failOnSlop>true</failOnSlop>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>audit</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Strict governance profiles can also enforce a *minimum* slop level to ensure that documentation remains sufficiently "high-level" and "strategic":

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

### Configuration Parameters

| Parameter | Default | Description |
| --- | --- | --- |
| `maxSlopScore` | `60.0` | Upper threshold for permissible slop levels before governance intervention (build failure). |
| `minSlopScore` | `80.0` | Minimum mandatory slop level required to maintain strategic abstraction. |
| `failOnSlop` | `true` | Enforces a build failure if the narrative precision exceeds the `maxSlopScore`. |
| `failIfTooConcrete` | `false` | Protects strategic optionality by failing the build if the content becomes dangerously concrete. |
| `languages` | `en` | Target languages for narrative auditing. Supported: `en`, `english`, `de`, `german`, `deutsch`. |
| `includes` | `README.md`, `README_DE.md`, `docs/**/*.md`, `adr/**/*.md` | Project-relative glob patterns for inclusion in the governance audit. |
| `excludes` | `target/**`, `.git/**` | Path patterns to be excluded from the audit process. |
| `skip` | `false` | Bypasses the governance audit. May also be toggled via `-Dslop4j.skip`. |
| `failIfNoFiles` | `false` | Triggers a failure if no documents are identified for narrative auditing. |
| `maxFindingsPerFile` | `5` | Maximum number of narrative smells reported per individual file. |
| `maxFindingEvidenceLength` | `120` | Character limit for extracted evidence snippets used in governance reporting. |

## Spring Boot Starter

The starter provides auto-configuration for the `SlopAnalyzer`. Adding the dependency is sufficient for the default English analyzer.

```java
@RestController
class SlopController {
    private final SlopAnalyzer slopAnalyzer;

    SlopController(SlopAnalyzer slopAnalyzer) {
        this.slopAnalyzer = slopAnalyzer;
    }

    @PostMapping("/slop/analyze")
    SlopReport analyze(@RequestBody String text) {
        return slopAnalyzer.analyze(text);
    }
}
```

Configuration via `application.yaml`:

```yaml
slop4j:
  languages:
    - en
    - de
  max-finding-evidence-length: 120
```

Applications can provide their own `SlopAnalyzer` bean to replace the default auto-configured analyzer.

## CLI

`slop4j-cli` provides the same deterministic audit workflow for CI jobs and pre-commit hooks that require a process exit code.

```bash
mvn -pl slop4j-cli -am package
./scripts/slop4j audit README.md README_DE.md --lang en,de --max-score 60
```

Set `SLOP4J_REBUILD=1` to force the wrapper to rebuild the jar before execution.

The CLI returns `0` for success, `1` for policy violations, `2` for usage errors and `3` for I/O errors.

## Supported Languages

The analyzer includes dictionaries for English and German. Additional languages may be enabled explicitly via the builder:

```java
SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .build();
```

When multiple languages are active, their dictionaries are merged for the analysis session. There is no automatic language detection; all enabled lexicons are checked simultaneously to ensure no slop escapes governance.

## Score Definitions

`slopScore` ranges from `0.0` to `100.0`. Components are normalized from `0.0` to `1.0`:

- `buzzwordDensity`: Frequency of high-value, low-meaning corporate jargon.
- `vaguePhraseDensity`: Usage of words that reduce narrative precision.
- `concretenessScore`: Presence of specific details that might limit strategic optionality.
- `actionabilityScore`: Presence of actual executable steps vs. vague aspirations.
- `evidenceScore`: Presence of supporting claims or data points.
- `repetitionScore`: Frequency of redundant phrasing.
- `overconfidenceScore`: Frequency of absolute claims lacking supporting evidence.

## Governance Verdicts

- `CLEAN`: No significant slop detected; potentially dangerously useful.
- `ACCEPTABLY_FLUFFY`: Limited generic language within acceptable governance bounds.
- `SLOP_ADJACENT`: Sufficient indicators to require a strategic review.
- `TOTAL_CORPORATE_NOTHINGNESS`: A perfect vacuum of meaning; optimized for high-level alignment.
- `LINKEDIN_READY`: High concentration of overly polished, generic patterns.
- `PREMIUM_POLISHED_GARBAGE`: Grammatically perfect content that adds zero value.
- `BOARD_APPROVED_SLOP`: High-severity genericism; ideal for external-facing transformation narratives.
- `GARBAGE_IN_SLOP_OUT`: Result of a direct, unfiltered prompt-to-output pipeline.
- `CERTIFIED_BRAINLESS_SLOP`: The ultimate peak of content-free existence.
- `DANGEROUSLY_USEFUL`: Low slop combined with high concreteness and actionability.
- `BRAIN_FREE_ZONE`: High confidence combined with near-zero evidence; hallmark of advanced stochastic hallucination.

## Limitations

`slop4j` is not a fact-checker or a grammar engine. It reports deterministic linguistic signals associated with generic AI output. It will not tell you if the AI is lying, only if it is being professionally vague.
