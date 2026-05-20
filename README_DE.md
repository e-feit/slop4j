# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

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

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

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

## Unterstützte Sprachen

Step 1 unterstützt englische und deutsche Wörterbücher. Der Standard-Analyzer
verwendet Englisch. Weitere Sprachen können explizit konfiguriert werden:

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
- `LINKEDIN_READY`: eine hohe Konzentration vager, generischer oder übermäßig
  polierter Sprachmuster.
- `BOARD_APPROVED_SLOP`: der höchste reguläre Slop-Score-Bereich, dominiert von
  generischer, wenig spezifischer oder unzureichend handlungsorientierter
  Sprache.
- `DANGEROUSLY_USEFUL`: ein niedriger Slop-Score kombiniert mit starken Signalen
  für Konkretheit und Handlungsorientierung.

## Grenzen

`slop4j` ist kein Halluzinationsdetektor, Grammatikprüfer, Quellenverifizierer
oder semantischer Wahrheitsmechanismus. Es meldet deterministische Signale wie
Buzzword-Dichte, Verwendung vager Phrasen, konkrete Anker, ausführbare
Handlungen, Wiederholung und übermäßig selbstsichere Claim-Marker.
