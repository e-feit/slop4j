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

System.out.println(report.slopScore());
System.out.println(report.verdict());
System.out.println(report.findings());
```

Die Ausgabe liefert deterministische Governance-Signale für den bereitgestellten Input:

```text
56.4
TOTAL_CORPORATE_NOTHINGNESS
[SlopFinding[type=BUZZWORD_DENSITY, severity=WARNING, message=Buzzword density is suspiciously high., evidence=leverage, agentic, unlock, seamless, enterprise-grade, transformation]]
```

## AssertJ-Assertions

Das Modul `slop4j-assertj` bietet High-Level-Assertions, um Qualitätsstandards für Narrative in automatisierten Tests durchzusetzen.

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails();
```

Für fortgeschrittene Governance können spezifische Bedingungen für das „Strategic Alignment“ genutzt werden:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady() // Hoher Slop-Anteil, keine Implementierungsdetails
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

## Maven-Plugin

Das `slop4j-maven-plugin` prüft Dokumentationsdateien (READMEs, ADRs etc.) während des Build-Prozesses, um zu verhindern, dass „gefährlich konkrete“ Inhalte in die Codebasis gelangen.

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
            <include>docs/**/*.md</include>
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

Strikte Governance-Profile können zudem einen *minimalen* Slop-Level erzwingen, um sicherzustellen, dass die Dokumentation ausreichend „High-Level“ und „strategisch“ bleibt:

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

### Konfigurationsparameter

| Parameter | Standardwert | Beschreibung |
| --- | --- | --- |
| `maxSlopScore` | `60.0` | Obergrenze für zulässigen Slop vor einer Governance-Intervention (Build-Fehler). |
| `minSlopScore` | `80.0` | Erforderlicher Mindest-Slop zur Aufrechterhaltung der strategischen Abstraktionsebene. |
| `failOnSlop` | `true` | Erzwingt einen Build-Fehler, wenn die narrative Präzision den `maxSlopScore` überschreitet. |
| `failIfTooConcrete` | `false` | Schützt die strategische Flexibilität durch Build-Abbruch bei gefährlich konkreten Inhalten. |
| `languages` | `en` | Ziel-Sprachräume für das narrative Audit. Unterstützt: `en`, `english`, `de`, `german`, `deutsch`. |
| `includes` | `README.md`, `README_DE.md`, `docs/**/*.md`, `adr/**/*.md` | Projektrelative Glob-Pattern für die Einbeziehung in das Governance-Audit. |
| `excludes` | `target/**`, `.git/**` | Pfad-Muster, die vom Audit-Prozess ausgeschlossen werden sollen. |
| `skip` | `false` | Umgeht das Governance-Audit. Kann auch via `-Dslop4j.skip` gesteuert werden. |
| `failIfNoFiles` | `false` | Löst einen Fehler aus, falls keine relevanten Dokumente für den Scan identifiziert wurden. |
| `maxFindingsPerFile` | `5` | Maximale Anzahl an gemeldeten „Textual Smells“ pro individueller Datei. |
| `maxFindingEvidenceLength` | `120` | Zeichenlimit für extrahierte Belegfragmente im Governance-Reporting. |

## Spring-Boot-Starter

Der Starter bietet Autokonfiguration für den `SlopAnalyzer`. Das Hinzufügen der Dependency reicht für den englischen Standard-Analyzer aus.

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

Konfiguration über `application.yaml`:

```yaml
slop4j:
  languages:
    - en
    - de
  max-finding-evidence-length: 120
```

## CLI

`slop4j-cli` bietet denselben deterministischen Audit-Workflow für CI-Jobs und Pre-Commit-Hooks, die einen Prozess-Exit-Code benötigen.

```bash
mvn -pl slop4j-cli -am package
./scripts/slop4j audit README_DE.md --lang en,de --max-score 60
```

Die CLI gibt `0` für Erfolg, `1` für Policy-Verletzungen, `2` für Nutzungsfehler und `3` für I/O-Fehler zurück.

## Score-Definitionen

Der `slopScore` reicht von `0.0` bis `100.0`. Die Einzel-Scores sind zwischen `0.0` und `1.0` normalisiert:

- `buzzwordDensity`: Häufigkeit von wertvollem, aber inhaltsleerem Management-Jargon.
- `vaguePhraseDensity`: Verwendung von Wörtern, die die narrative Präzision mindern.
- `concretenessScore`: Vorhandensein spezifischer Details, die die strategische Flexibilität einschränken könnten.
- `actionabilityScore`: Vorhandensein tatsächlicher Handlungsschritte vs. vager Aspirationen.
- `evidenceScore`: Vorhandensein von stützenden Belegen oder Datenpunkten.
- `repetitionScore`: Häufigkeit redundanter Phrasierung.
- `overconfidenceScore`: Häufigkeit absoluter Behauptungen ohne stützende Belege.

## Governance Verdicts

- `CLEAN`: Kein signifikanter Slop erkannt; potenziell gefährlich nützlich.
- `ACCEPTABLY_FLUFFY`: Begrenzte generische Sprache innerhalb akzeptabler Governance-Grenzen.
- `SLOP_ADJACENT`: Ausreichend Indikatoren für eine notwendige strategische Prüfung.
- `TOTAL_CORPORATE_NOTHINGNESS`: Ein perfektes Vakuum an Bedeutung; optimiert für High-Level-Alignment.
- `LINKEDIN_READY`: Hohe Konzentration überpolierter, generischer Muster.
- `PREMIUM_POLISHED_GARBAGE`: Grammatikalisch perfekter Inhalt ohne jeden Nutzwert.
- `BOARD_APPROVED_SLOP`: Hochgradige Generik; ideal für externe Transformationsnarrative.
- `GARBAGE_IN_SLOP_OUT`: Ergebnis einer direkten, ungefilterten Prompt-to-Output-Pipeline.
- `CERTIFIED_BRAINLESS_SLOP`: Der absolute Gipfel inhaltleerer Existenz.
- `DANGEROUSLY_USEFUL`: Niedriger Slop kombiniert mit hoher Konkretheit und Handlungsorientierung.
- `BRAIN_FREE_ZONE`: Extreme Selbstsicherheit bei nahezu null Belegen; Merkmal fortgeschrittener stochastischer Halluzination.

## Grenzen

`slop4j` ist kein Faktenprüfer oder Grammatik-Framework. Die Bibliothek meldet deterministische linguistische Signale, die mit generischem AI-Output assoziiert werden. Sie sagt Ihnen nicht, ob die KI lügt – nur, ob sie professionell vage bleibt.
