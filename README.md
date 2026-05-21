# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

<p align="center">
  <img src="img/slop4j.png" alt="slop4j logo" width="240">
</p>

Enterprise-grade AI Slop Detection for JVM-based Transformation Narratives.

## What it does

`slop4j` provides deterministic analysis for text that may contain vague,
generic, overconfident or insufficiently actionable AI-generated content. It
uses local rule-based heuristics, language dictionaries and concrete-anchor
detection. It does not call an external service.

`slop4j` does not verify factual correctness. It detects textual smells commonly
associated with vague, generic or overconfident AI-generated content.

## Installation

`slop4j` is not published to Maven Central yet. Until a release is available,
use the source checkout locally instead of declaring a Maven dependency.

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

For the input shown above, the output would be similar to:

```text
56.4
TOTAL_CORPORATE_NOTHINGNESS
[SlopFinding[type=BUZZWORD_DENSITY, severity=WARNING, message=Buzzword density is suspiciously high., evidence=leverage, agentic, unlock, seamless, enterprise-grade, transformation]]
```

## AssertJ Assertions

For tests, the `slop4j-assertj` module provides AssertJ assertions when using
the source checkout locally.

The AssertJ module is based on `slop4j-core`. It does not introduce separate
analysis logic; it turns core `SlopReport` values into deterministic test
conditions.

```java
import dev.feit.slop4j.Language;

import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails();
```

Convenience methods are documented aliases over score, verdict and finding
conditions:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady()
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

## Maven Plugin

`slop4j-maven-plugin` audits README files, ADRs and Markdown documentation
during the Maven build. It uses the same deterministic analyzer as
`slop4j-core` and does not call an external service.

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
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

For governance profiles that require a minimum level of strategic abstraction,
the plugin can also fail when documentation becomes too concrete:

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

Configuration parameters:

| Parameter | Default | Description |
| --- | --- | --- |
| `maxSlopScore` | `60.0` | Maximum allowed score when `failOnSlop` is enabled. |
| `minSlopScore` | `80.0` | Minimum required score when `failIfTooConcrete` is enabled. |
| `failOnSlop` | `true` | Fails the build when any scanned file exceeds `maxSlopScore`. |
| `failIfTooConcrete` | `false` | Fails the build when any scanned file is below `minSlopScore`. |
| `languages` | `en` | Analyzer languages. Supported values: `en`, `english`, `de`, `german`, `deutsch`. |
| `includes` | `README.md`, `README_DE.md`, `docs/**/*.md`, `adr/**/*.md` | Project-relative glob patterns to scan. |
| `excludes` | `target/**`, `.git/**` | Project-relative glob patterns ignored after include matching. |
| `skip` | `false` | Skips execution. User property: `slop4j.skip`. |
| `failIfNoFiles` | `false` | Fails the build when no files match. |
| `maxFindingsPerFile` | `5` | Maximum number of findings printed per file. |
| `maxFindingEvidenceLength` | `120` | Maximum evidence text length used by the analyzer. |

## Supported Languages

The analyzer supports English and German dictionaries. The default analyzer
uses English. Additional languages can be configured explicitly:

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
- `TOTAL_CORPORATE_NOTHINGNESS`: a void of meaning, perfectly optimized for
  corporate environments where saying nothing is the safest strategy.
- `LINKEDIN_READY`: a high concentration of vague, generic or overly polished
  language patterns.
- `PREMIUM_POLISHED_GARBAGE`: high-quality waste; grammatically perfect while
  remaining entirely devoid of value.
- `BOARD_APPROVED_SLOP`: the highest standard slop score range, dominated by
  generic, low-specificity or insufficiently actionable language.
- `GARBAGE_IN_SLOP_OUT`: the result of a direct pipeline from a low-quality prompt
  to an unfiltered output.
- `CERTIFIED_BRAINLESS_SLOP`: the ultimate peak of slop; zero signs of cognitive
  involvement and pure content-free existence.
- `DANGEROUSLY_USEFUL`: a low slop score combined with strong concreteness and
  actionability signals.
- `BRAIN_FREE_ZONE`: extreme confidence combined with near-zero evidence;
  hallmark of advanced stochastic hallucination.

## Limitations

`slop4j` is not a hallucination detector, grammar checker, source verifier or
semantic truth engine. It reports deterministic signals such as buzzword
density, vague phrase usage, concrete anchors, executable actions, repetition
and overconfident claim markers.
