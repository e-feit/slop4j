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

System.out.println(report.slopScore()); // 56.4
System.out.println(report.verdict());  // TOTAL_CORPORATE_NOTHINGNESS
```

## AssertJ Assertions

The `slop4j-assertj` module provides high-level assertions to enforce narrative quality standards in automated tests.

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH)
    .hasSlopScoreBelow(40.0)
    .containsConcreteDetails();
```

For advanced governance, use specific strategic alignment conditions:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady() // High slop, zero implementation details
    .maximizesPlausibleDeniability();
```

## Maven Plugin

The `slop4j-maven-plugin` audits documentation files (READMEs, ADRs, etc.) during the build process to prevent "dangerously concrete" content from entering the codebase.

```xml
<configuration>
    <maxSlopScore>60.0</maxSlopScore>
    <failOnSlop>true</failOnSlop>
    <includes>
        <include>README.md</include>
        <include>docs/**/*.md</include>
    </includes>
</configuration>
```

Strict governance profiles can also enforce a *minimum* slop level to ensure that documentation remains sufficiently "high-level" and "strategic":

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

## Score Definitions

- `buzzwordDensity`: Frequency of high-value, low-meaning corporate jargon.
- `concretenessScore`: Detection of specific details that might limit strategic optionality.
- `actionabilityScore`: Presence of actual executable steps vs. vague aspirations.
- `overconfidenceScore`: Frequency of absolute claims lacking supporting evidence.

## Governance Verdicts

- `CLEAN`: No significant slop detected; potentially dangerously useful.
- `TOTAL_CORPORATE_NOTHINGNESS`: A perfect vacuum of meaning; optimized for high-level alignment.
- `PREMIUM_POLISHED_GARBAGE`: Grammatically perfect content that adds zero value.
- `BOARD_APPROVED_SLOP`: High-severity genericism; ideal for external-facing transformation narratives.
- `BRAIN_FREE_ZONE`: High confidence combined with near-zero evidence; hallmark of advanced stochastic hallucination.

## Limitations

`slop4j` is not a fact-checker. It reports deterministic linguistic signals associated with generic AI output. It will not tell you if the AI is lying, only if it is being professionally vague.
