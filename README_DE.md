# slop4j

[English](./README.md) | [Deutsch](./README_DE.md)

<p align="center">
  <img src="img/slop4j.png" alt="slop4j logo" width="240">
</p>

AI-Slop-Erkennung und Governance für JVM-Anwendungen.

## Überblick

`slop4j` identifiziert vage, generische oder übermäßig selbstsichere Formulierungen, wie sie für viele AI-generierte Texte typisch sind. Die Bibliothek nutzt regelbasierte Heuristiken und lokale Wörterbücher, um diese Muster ohne externe API-Aufrufe zu finden.

Das Framework prüft keine sachliche Korrektheit. Der Fokus liegt auf der Erkennung von „textuellen Smells“ – Merkmalen geringer Spezifität und generischer Phrasierung, die den Nutzwert generierter Texte mindern.

## Anforderungen

- Java 17 oder höher

## Installation

<!-- slop4j-installation:start -->
Aktuelle Version: `0.2.1`.

Core-Bibliothek:

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-core</artifactId>
    <version>0.2.1</version>
</dependency>
```

AssertJ-Assertions für Tests:

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-assertj</artifactId>
    <version>0.2.1</version>
    <scope>test</scope>
</dependency>
```

Maven-Plugin:

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.2.1</version>
</plugin>
```

Spring-Boot-Starter:

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

System.out.println(report.slopScore());
System.out.println(report.verdict());
System.out.println(report.findings());
```

Beispielhafte Ausgabe:

```text
56.4
TOTAL_CORPORATE_NOTHINGNESS
[SlopFinding[type=BUZZWORD_DENSITY, severity=WARNING, message=Buzzword density is suspiciously high., evidence=leverage, agentic, unlock, seamless, enterprise-grade, transformation]]
```

## AssertJ-Assertions

Das Modul `slop4j-assertj` bietet eigene Assertions, um die Qualität generierter Texte in automatisierten Tests sicherzustellen.

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails();
```

Das Modul enthält zudem High-Level-Bedingungen für Governance-Anforderungen:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady()
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

## Maven-Plugin

Das `slop4j-maven-plugin` prüft Dokumentationsdateien während des Build-Prozesses.

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

Für strikte Governance kann das Plugin auch ein Mindestmaß an Slop erzwingen, um eine ausreichende strategische Abstraktion sicherzustellen:

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

### Konfigurationsparameter

| Parameter | Standardwert | Beschreibung |
| --- | --- | --- |
| `maxSlopScore` | `60.0` | Maximal erlaubter Score vor dem Build-Fehler. |
| `minSlopScore` | `80.0` | Minimal erforderlicher Score, wenn `failIfTooConcrete` aktiv ist. |
| `failOnSlop` | `true` | Lässt den Build fehlschlagen, wenn eine Datei `maxSlopScore` überschreitet. |
| `failIfTooConcrete` | `false` | Lässt den Build fehlschlagen, wenn eine Datei unter `minSlopScore` liegt. |
| `languages` | `en` | Zielsprachen (`en`, `de`). |
| `includes` | `README.md`, ... | Glob-Pattern für zu scannende Dateien. |
| `excludes` | `target/**`, ... | Zu ignorierende Glob-Pattern. |
| `skip` | `false` | Deaktiviert das Plugin. |
| `failIfNoFiles` | `false` | Fehler, wenn keine passenden Dateien gefunden werden. |
| `maxFindingsPerFile` | `5` | Maximale Anzahl an Findings pro Datei. |

## Spring-Boot-Starter

`slop4j-spring-boot-starter` bietet die Autokonfiguration für den `SlopAnalyzer`.

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

Konfiguration über `application.properties` oder `application.yaml`:

```yaml
slop4j:
  languages:
    - en
    - de
  max-finding-evidence-length: 120
```

## CLI

Das `slop4j-cli` ermöglicht manuelle Prüfungen und die Integration in CI-Pipelines außerhalb von Maven.

```bash
mvn -pl slop4j-cli -am package
./scripts/slop4j audit README.md --lang en,de --max-score 60
```

## Unterstützte Sprachen

Der Analyzer enthält Wörterbücher für Englisch und Deutsch. Bei Aktivierung mehrerer Sprachen werden diese für die Analyse zusammengeführt.

## Score-Definitionen

Der `slopScore` reicht von `0.0` bis `100.0`. Einzel-Scores sind zwischen `0.0` und `1.0` normalisiert:

- `buzzwordDensity`: Häufigkeit von generischem Corporate-Jargon.
- `vaguePhraseDensity`: Verwendung von Wörtern mit geringem semantischem Gehalt.
- `concretenessScore`: Vorhandensein spezifischer, prüfbarer Details.
- `actionabilityScore`: Klarheit von Anweisungen oder ausführbaren Schritten.
- `evidenceScore`: Vorhandensein von Belegen oder Datenpunkten.
- `repetitionScore`: Häufigkeit redundanter Phrasierung.
- `overconfidenceScore`: Häufigkeit absoluter Behauptungen ohne Belege.

## Verdicts

- `CLEAN`: Keine wesentlichen Indikatoren gefunden.
- `ACCEPTABLY_FLUFFY`: Typische Corporate-Sprache.
- `SLOP_ADJACENT`: Deutliche Präsenz generischer Muster.
- `TOTAL_CORPORATE_NOTHINGNESS`: Kein semantischer Gehalt erkennbar.
- `LINKEDIN_READY`: Hohe Konzentration überpolierter Phrasen.
- `PREMIUM_POLISHED_GARBAGE`: Grammatikalisch korrekt, aber wertfrei.
- `BOARD_APPROVED_SLOP`: Kritische Slop-Indikatoren.
- `GARBAGE_IN_SLOP_OUT`: Direkte Pipeline von minderwertigem Input.
- `CERTIFIED_BRAINLESS_SLOP`: Maximaler Slop-Level.
- `DANGEROUSLY_USEFUL`: Hohe Konkretheit und Handlungsorientierung.
- `BRAIN_FREE_ZONE`: Hohe Zuversicht ohne stützende Belege.

## Grenzen

`slop4j` ist kein Faktenprüfer oder semantisches Analyse-Framework. Es identifiziert deterministische linguistische Muster, die häufig mit generischem AI-Output assoziiert werden.
