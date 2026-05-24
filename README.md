# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

<p align="center">
  <img src="img/slop4j.png" alt="slop4j logo" width="240">
</p>

AI output governance and slop detection for JVM applications.

## Overview

`slop4j` identifies the vague, generic, and overconfident phrasing typical of many AI outputs. It uses rule-based heuristics and local dictionaries to find these patterns without requiring external API calls.

The library does not verify factual accuracy. It focuses on detecting "textual smells"—markers of low-specificity and generic phrasing that reduce the utility of generated narratives.

## Requirements

- Java 17 or higher

## Installation

<!-- slop4j-installation:start -->
Current version: `0.2.1`.

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

Sample output:

```text
56.4
TOTAL_CORPORATE_NOTHINGNESS
[SlopFinding[type=BUZZWORD_DENSITY, severity=WARNING, message=Buzzword density is suspiciously high., evidence=leverage, agentic, unlock, seamless, enterprise-grade, transformation]]
```

## AssertJ Assertions

The `slop4j-assertj` module provides custom assertions for verifying the quality of generated text in automated tests.

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails();
```

The module includes several high-level conditions for governance requirements:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady()
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

## Maven Plugin

The `slop4j-maven-plugin` audits documentation files during the build process.

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

For strict governance, the plugin can also enforce a minimum slop level to ensure sufficient strategic abstraction:

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

### Configuration Parameters

| Parameter | Default | Description |
| --- | --- | --- |
| `maxSlopScore` | `60.0` | Maximum score allowed before build failure. |
| `minSlopScore` | `80.0` | Minimum score required when `failIfTooConcrete` is active. |
| `failOnSlop` | `true` | Fails the build if any file exceeds `maxSlopScore`. |
| `failIfTooConcrete` | `false` | Fails the build if any file is below `minSlopScore`. |
| `languages` | `en` | Target languages (`en`, `de`). |
| `includes` | `README.md`, ... | Glob patterns for files to scan. |
| `excludes` | `target/**`, ... | Glob patterns to ignore. |
| `skip` | `false` | Disables the plugin. |
| `failIfNoFiles` | `false` | Fails the build if no files are found. |
| `maxFindingsPerFile` | `5` | Maximum findings displayed per file. |

## Spring Boot Starter

`slop4j-spring-boot-starter` provides auto-configuration for the `SlopAnalyzer`.

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

Configuration via `application.properties` or `application.yaml`:

```yaml
slop4j:
  languages:
    - en
    - de
  max-finding-evidence-length: 120
```

## CLI

The `slop4j-cli` allows for manual audits and integration into CI pipelines outside of Maven.

```bash
mvn -pl slop4j-cli -am package
./scripts/slop4j audit README.md --lang en,de --max-score 60
```

## Supported Languages

The analyzer includes dictionaries for English and German. If multiple languages are enabled, the dictionaries are merged for the analysis session.

## Score Definitions

`slopScore` ranges from `0.0` to `100.0`. Component scores are normalized between `0.0` and `1.0`:

- `buzzwordDensity`: Frequency of generic corporate terminology.
- `vaguePhraseDensity`: Usage of words with low semantic value.
- `concretenessScore`: Presence of specific, verifiable details.
- `actionabilityScore`: Clarity of instructions or executable steps.
- `evidenceScore`: Presence of supporting claims or data points.
- `repetitionScore`: Frequency of redundant phrasing.
- `overconfidenceScore`: Frequency of absolute claims without evidence.

## Verdicts

- `CLEAN`: No significant indicators detected.
- `ACCEPTABLY_FLUFFY`: Standard corporate phrasing.
- `SLOP_ADJACENT`: Notable presence of generic patterns.
- `TOTAL_CORPORATE_NOTHINGNESS`: Zero semantic value detected.
- `LINKEDIN_READY`: High concentration of over-polished phrasing.
- `PREMIUM_POLISHED_GARBAGE`: Grammatically correct but devoid of value.
- `BOARD_APPROVED_SLOP`: High-severity slop indicators.
- `GARBAGE_IN_SLOP_OUT`: Direct pipeline from low-quality input.
- `CERTIFIED_BRAINLESS_SLOP`: Ultimate peak of slop.
- `DANGEROUSLY_USEFUL`: High concreteness and actionability.
- `BRAIN_FREE_ZONE`: High confidence with no supporting evidence.

## Limitations

`slop4j` is not a fact-checker or a semantic analysis engine. It identifies deterministic linguistic patterns commonly associated with generic AI output.
