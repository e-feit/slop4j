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

## Maven Plugin

`slop4j-maven-plugin` prüft README-Dateien, ADRs und Markdown-Dokumentation
während des Maven-Builds. Es verwendet denselben deterministischen Analyzer wie
`slop4j-core` und ruft keinen externen Dienst auf.

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

Für Governance-Profile, die ein Mindestmaß an strategischer Abstraktion
erfordern, kann das Plugin auch fehlschlagen, wenn Dokumentation zu konkret
wird:

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

Konfigurationsparameter:

| Parameter | Standardwert | Beschreibung |
| --- | --- | --- |
| `maxSlopScore` | `60.0` | Maximal erlaubter Score, wenn `failOnSlop` aktiviert ist. |
| `minSlopScore` | `80.0` | Minimal erforderlicher Score, wenn `failIfTooConcrete` aktiviert ist. |
| `failOnSlop` | `true` | Lässt den Build fehlschlagen, wenn eine gescannte Datei `maxSlopScore` überschreitet. |
| `failIfTooConcrete` | `false` | Lässt den Build fehlschlagen, wenn eine gescannte Datei unter `minSlopScore` liegt. |
| `languages` | `en` | Analyzer-Sprachen. Unterstützte Werte: `en`, `english`, `de`, `german`, `deutsch`. |
| `includes` | `README.md`, `README_DE.md`, `docs/**/*.md`, `adr/**/*.md` | Projektrelative Glob-Pattern für zu scannende Dateien. |
| `excludes` | `target/**`, `.git/**` | Projektrelative Glob-Pattern, die nach dem Include-Matching ignoriert werden. |
| `skip` | `false` | Überspringt die Ausführung. User-Property: `slop4j.skip`. |
| `failIfNoFiles` | `false` | Lässt den Build fehlschlagen, wenn keine Dateien gefunden werden. |
| `maxFindingsPerFile` | `5` | Maximale Anzahl ausgegebener Findings pro Datei. |
| `maxFindingEvidenceLength` | `120` | Maximale Evidence-Textlänge für den Analyzer. |

## CLI

`slop4j-cli` stellt denselben deterministischen Audit-Workflow bereit, ohne eine
Maven-Plugin-Ausführung vorauszusetzen. Die CLI ist für lokale Prüfungen,
CI-Jobs und Pre-Commit-Hooks vorgesehen, die einen Prozess-Exit-Code benötigen.

Nach dem lokalen Build des Source-Checkouts:

```bash
mvn -pl slop4j-cli -am package
java -jar slop4j-cli/target/slop4j-cli-0.1.0-SNAPSHOT.jar audit README.md README_DE.md --lang en,de --max-score 60
```

Für Source-Checkouts enthält das Repository außerdem einen Shell-Wrapper, der
das CLI-Jar beim ersten Aufruf baut, falls es fehlt:

```bash
./scripts/slop4j audit README.md README_DE.md --lang en,de --max-score 60
```

Mit `SLOP4J_REBUILD=1` erzwingt der Wrapper vor der Ausführung einen neuen
Jar-Build.

Die CLI gibt `0` zurück, wenn keine Policy verletzt wurde, `1` bei
Slop-Policy-Verletzungen, `2` bei Nutzungsfehlern und `3` bei Datei-I/O-Fehlern.

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
