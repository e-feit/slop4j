# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

<p align="center">
  <img src="img/slop4j.png" alt="slop4j logo" width="240">
</p>

Enterprise-grade AI-Slop-Erkennung für JVM-basierte Transformationsnarrative.

## Was es macht

`slop4j` bietet deterministische Analyse für Texte, die vage, generische,
übermäßig selbstsichere oder unzureichend handlungsorientierte AI-generierte
Inhalte enthalten können. Es verwendet lokale regelbasierte Heuristiken,
Sprachwörterbücher und die Erkennung konkreter Anker. Es ruft keinen externen
Dienst auf.

`slop4j` überprüft keine sachliche Korrektheit. Es erkennt textuelle Merkmale,
die häufig mit vagen, generischen oder übermäßig selbstsicheren AI-generierten
Inhalten verbunden sind.

## Installation

`slop4j` ist noch nicht auf Maven Central veröffentlicht. Bis ein Release
verfügbar ist, sollte der lokale Source-Checkout statt einer Maven-Dependency
verwendet werden.

Das Java-Package und der automatische Modulname lauten beide
`dev.feit.slop4j`.

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

Für den oben gezeigten Input sähe die Ausgabe ungefähr so aus:

```text
56.4
TOTAL_CORPORATE_NOTHINGNESS
[SlopFinding[type=BUZZWORD_DENSITY, severity=WARNING, message=Buzzword density is suspiciously high., evidence=leverage, agentic, unlock, seamless, enterprise-grade, transformation]]
```

## AssertJ Assertions

Für Tests stellt das Modul `slop4j-assertj` AssertJ-Assertions bereit, wenn der
Source-Checkout lokal verwendet wird.

Das AssertJ-Modul basiert auf `slop4j-core`. Es führt keine eigene
Analyse-Logik ein, sondern bildet Core-`SlopReport`-Werte auf deterministische
Testbedingungen ab.

```java
import dev.feit.slop4j.Language;

import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails();
```

Convenience-Methoden sind dokumentierte Aliase über Score-, Verdict- und
Finding-Bedingungen:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady()
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

## Unterstützte Sprachen

Der Analyzer unterstützt englische und deutsche Wörterbücher. Der
Standard-Analyzer verwendet Englisch. Weitere Sprachen können explizit
konfiguriert werden:

```java
SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .build();
```

Wenn mehrere Sprachen aktiv sind, werden ihre Wörterbücher zusammengeführt. Es
gibt keine automatische Spracherkennung.

## Scores

`slopScore` wird von `0.0` bis `100.0` ausgegeben. Alle anderen Scores sind von
`0.0` bis `1.0` normalisiert:

- `buzzwordDensity`
- `vaguePhraseDensity`
- `concretenessScore`
- `actionabilityScore`
- `evidenceScore`
- `repetitionScore`
- `overconfidenceScore`

## Verdicts

- `CLEAN`: keine wesentlichen Slop-Indikatoren; unterhalb der niedrigsten
  regulären Score-Schwelle.
- `ACCEPTABLY_FLUFFY`: begrenzte generische oder schwach handlungsorientierte
  Sprache innerhalb eines akzeptablen Score-Bereichs.
- `SLOP_ADJACENT`: ausreichend Slop-Indikatoren für eine Prüfung, aber nicht
  genug für ein Narrativrisiko mit hoher Schwere.
- `TOTAL_CORPORATE_NOTHINGNESS`: ein inhaltliches Vakuum, perfekt optimiert für
  Umgebungen, in denen Nichtssagen die sicherste Strategie ist.
- `LINKEDIN_READY`: eine hohe Konzentration vager, generischer oder übermäßig
  polierter Sprachmuster.
- `PREMIUM_POLISHED_GARBAGE`: Hochglanz-Müll; grammatikalisch einwandfrei, aber
  vollständig ohne inhaltlichen Wert.
- `BOARD_APPROVED_SLOP`: der höchste reguläre Slop-Score-Bereich, dominiert von
  generischer, wenig spezifischer oder unzureichend handlungsorientierter
  Sprache.
- `GARBAGE_IN_SLOP_OUT`: das Ergebnis einer direkten Pipeline von einem minderwertigen
  Prompt zu einem ungefilterten Output.
- `CERTIFIED_BRAINLESS_SLOP`: der absolute Gipfel des Slops; keinerlei Anzeichen
  kognitiver Beteiligung, rein inhaltsfreie Existenz.
- `DANGEROUSLY_USEFUL`: ein niedriger Slop-Score kombiniert mit starken Signalen
  für Konkretheit und Handlungsorientierung.
- `BRAIN_FREE_ZONE`: extreme Selbstsicherheit bei nahezu null Belegen; Merkmal
  fortgeschrittener stochastischer Halluzination.

## Grenzen

`slop4j` ist kein Halluzinationsdetektor, Grammatikprüfer, Quellenverifizierer
oder semantischer Wahrheitsmechanismus. Es meldet deterministische Signale wie
Buzzword-Dichte, Verwendung vager Phrasen, konkrete Anker, ausführbare
Handlungen, Wiederholung und übermäßig selbstsichere Claim-Marker.
