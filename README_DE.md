# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

<p align="center">
  <img src="img/slop4j.png" alt="slop4j logo" width="240">
</p>

Enterprise-grade AI-Output-Governance und deterministische Slop-Mitigation für JVM-basierte Transformationsnarrative.

## Überblick

`slop4j` bietet ein robustes, deterministisches Framework für das Audit von AI-generierten Inhalten. Es identifiziert hohe Konzentrationen vager Formulierungen, unbegründeter Selbstsicherheit und mangelnder Handlungsorientierung – typische Indikatoren für Texte mit geringer kognitiver Beteiligung.

Durch die Verwendung lokaler, regelbasierter Heuristiken und validierter Sprachwörterbücher ermöglicht `slop4j` Organisationen, ihr „Strategic Alignment“ abzusichern, ohne auf externe API-Aufrufe oder nicht-deterministische „KI-prüft-KI“-Analysen angewiesen zu sein.

Der Fokus liegt exklusiv auf „textuellen Smells“ – Merkmalen generischer Phrasierung, die den strategischen Nutzwert von Dokumentationen, ADRs und Transformations-Roadmaps mindern.

## Anforderungen

- Java 17 oder höher

## Installation

<!-- slop4j-installation:start -->
Aktuelle Version: `0.2.1`.

### Core-Bibliothek

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-core</artifactId>
    <version>0.2.1</version>
</dependency>
```

### AssertJ-Assertions

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-assertj</artifactId>
    <version>0.2.1</version>
    <scope>test</scope>
</dependency>
```

### Maven-Plugin

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.2.1</version>
</plugin>
```

### Spring-Boot-Starter

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-spring-boot-starter</artifactId>
    <version>0.2.1</version>
</dependency>
```
<!-- slop4j-installation:end -->

Das Java-Package und der automatische Modulname lauten `dev.feit.slop4j`.

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

## AssertJ-Assertions

Das Modul `slop4j-assertj` bietet High-Level-Assertions, um Qualitätsstandards für Narrative in automatisierten Tests durchzusetzen.

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .containsConcreteDetails();
```

Für fortgeschrittene Governance können spezifische Bedingungen für das „Strategic Alignment“ genutzt werden:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady() // Hoher Slop-Anteil, keine Implementierungsdetails
    .maximizesPlausibleDeniability();
```

## Maven-Plugin

Das `slop4j-maven-plugin` prüft Dokumentationsdateien (READMEs, ADRs etc.) während des Build-Prozesses, um zu verhindern, dass „gefährlich konkrete“ Inhalte in die Codebasis gelangen.

```xml
<configuration>
    <maxSlopScore>60.0</maxSlopScore>
    <failOnSlop>true</failOnSlop>
    <includes>
        <include>README_DE.md</include>
        <include>docs/**/*.md</include>
    </includes>
</configuration>
```

Strikte Governance-Profile können zudem einen *minimalen* Slop-Level erzwingen, um sicherzustellen, dass die Dokumentation ausreichend „High-Level“ und „strategisch“ bleibt:

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

## Score-Definitionen

- `buzzwordDensity`: Häufigkeit von wertvollem, aber inhaltsleerem Management-Jargon.
- `concretenessScore`: Erkennung spezifischer Details, die die strategische Flexibilität einschränken könnten.
- `actionabilityScore`: Vorhandensein tatsächlicher Handlungsschritte vs. vager Aspirationen.
- `overconfidenceScore`: Häufigkeit absoluter Behauptungen ohne stützende Belege.

## Governance Verdicts

- `CLEAN`: Kein signifikanter Slop erkannt; potenziell gefährlich nützlich.
- `TOTAL_CORPORATE_NOTHINGNESS`: Ein perfektes Vakuum an Bedeutung; optimiert für High-Level-Alignment.
- `PREMIUM_POLISHED_GARBAGE`: Grammatikalisch perfekter Inhalt ohne jeden Nutzwert.
- `BOARD_APPROVED_SLOP`: Hochgradige Generik; ideal für externe Transformationsnarrative.
- `BRAIN_FREE_ZONE`: Extreme Selbstsicherheit bei nahezu null Belegen; Merkmal fortgeschrittener stochastischer Halluzination.

## Grenzen

`slop4j` ist kein Faktenprüfer. Die Bibliothek meldet deterministische linguistische Signale, die mit generischem AI-Output assoziiert werden. Sie sagt Ihnen nicht, ob die KI lügt – nur, ob sie professionell vage bleibt.
