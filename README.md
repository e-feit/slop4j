# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

Enterprise-grade AI Slop Detection for JVM-based Transformation Narratives.

## What it does

`slop4j` provides deterministic analysis for text that may contain vague,
generic, overconfident or insufficiently actionable AI-generated content. It
uses local rule-based heuristics, language dictionaries and concrete-anchor
detection. It does not call an external service.

`slop4j` does not verify factual correctness. It detects textual smells commonly
associated with vague, generic or overconfident AI-generated content.

## Installation

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

The Java package and automatic module name are both `dev.feit.slop4j`.

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

## Supported Languages

Step 1 supports English and German dictionaries. The default analyzer uses
English. Additional languages can be configured explicitly:

```java
SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .build();
```

When multiple languages are active, their dictionaries are merged. There is no
automatic language detection.

## Scores

`slopScore` is reported from `0.0` to `100.0`. All other scores are normalized
from `0.0` to `1.0`:

- `buzzwordDensity`
- `vaguePhraseDensity`
- `concretenessScore`
- `actionabilityScore`
- `evidenceScore`
- `repetitionScore`
- `overconfidenceScore`

## Verdicts

- `CLEAN`: no material slop indicators; below the lowest standard score
  threshold.
- `ACCEPTABLY_FLUFFY`: limited generic or weakly actionable language within an
  acceptable score range.
- `SLOP_ADJACENT`: enough slop indicators to require attention, but not enough
  for a high-severity narrative risk.
- `LINKEDIN_READY`: a high concentration of vague, generic or overly polished
  language patterns.
- `BOARD_APPROVED_SLOP`: the highest standard slop score range, dominated by
  generic, low-specificity or insufficiently actionable language.
- `DANGEROUSLY_USEFUL`: a low slop score combined with strong concreteness and
  actionability signals.

## Limitations

`slop4j` is not a hallucination detector, grammar checker, source verifier or
semantic truth engine. It reports deterministic signals such as buzzword
density, vague phrase usage, concrete anchors, executable actions, repetition
and overconfident claim markers.
