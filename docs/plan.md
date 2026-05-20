# slop4j: Entwicklungsplan für eine scherzhafte Java-Library gegen AI Slop

## 1. Zielbild

`slop4j` ist eine kleine, deterministische Java-Library, die Texte auf typische Merkmale von „AI Slop“ analysiert. Die Library soll bewusst ohne LLM, ohne externe API und ohne Cloud-Abhängigkeit funktionieren. Der satirische Kern liegt darin, dass ein scheinbar hochprofessionelles „AI Output Governance Framework“ intern nur regelbasierte, transparente Heuristiken verwendet.

Der erste Ausbauschritt konzentriert sich ausschließlich auf den programmatischen Analyzer als Maven-einbindbare Java-Library.

Nicht Bestandteil von Step 1:

- kein Maven-Plugin
- kein Spring Boot Starter
- keine Annotationen
- kein Annotation Processor
- keine Sloppifizierung beziehungsweise Text-Transformation
- keine CLI
- keine externe NLP-Library
- keine AI-Integration

## 2. Produktidee in einem Satz

Eine Java-Library, die prüft, ob ein Text konkrete Information enthält oder nur nach „AI-powered enterprise-grade transformation narrative“ klingt.

## 3. Tonalität und Produktpositionierung

`slop4j` ist konzeptionell eine Scherz-Library: Sie nimmt den aktuellen AI-Slop-, Agentic-AI- und Enterprise-Governance-Hype bewusst ernst genug, um ihn ad absurdum zu führen. Der Kern soll aber nicht nur ein Witz sein. Der Analyzer soll tatsächlich brauchbare Hinweise liefern, ob ein Text zu vage, zu buzzword-lastig, zu wenig konkret oder kaum handlungsorientiert ist.

Die Doppelrolle ist wichtig:

- intern und im Entwicklungsplan darf klar sein, dass die Library satirisch gemeint ist
- die technische Umsetzung soll trotzdem sauber, deterministisch und testbar sein
- die API soll wie eine seriöse Java-Library wirken
- die README.md soll später ausdrücklich nicht erklären, dass es eine Scherz-Library ist
- die README.md soll stattdessen bewusst überernst klingen, als handele es sich um ein Enterprise-grade AI Output Quality Governance Framework

Der Humor entsteht aus der Diskrepanz zwischen überernster Außendarstellung und der banalen, transparenten, regelbasierten Implementierung. Deshalb sollte die öffentliche Dokumentation nicht albern wirken, sondern trocken, seriös und leicht überzogen. Beispiele dürfen absurd sein, aber der Ton sollte professionell bleiben.

## 4. Technische Prinzipien

Die Library soll folgende Eigenschaften haben:

- vollständig offline lauffähig
- deterministisch
- schnell genug für Dokumentations- und LLM-Output-Analyse
- nur minimale Runtime-Abhängigkeiten im Core; SnakeYAML ist für YAML-basierte Sprachdaten zugelassen
- einfache öffentliche API
- intern regelbasiert und erweiterbar
- sprachunterstützend über Ressourcen-Dateien
- zunächst Englisch und Deutsch
- bewusst nicht übermodelliert
- gute Testbarkeit der einzelnen Regeln

Empfohlene Java-Version: Java 17.

Begründung: Java 17 ist in Enterprise-Umgebungen breit genug verfügbar, unterstützt Records und moderne Sprachfeatures und passt gut zu Maven Central beziehungsweise typischen Firmenprojekten. Java 21 wäre technisch angenehmer, reduziert aber die potenzielle Nutzbarkeit in konservativeren Umgebungen.

## 5. Step 1: Core Analyzer Library

### 5.1 Ziel von Step 1

Step 1 liefert ein Maven-Artefakt, das programmatisch in Java-Projekten genutzt werden kann.

Beispielhafte Verwendung:

```java
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

Erwartete Ausgabe sinngemäß:

```text
Slop score: 87.4
Verdict: BOARD_APPROVED_SLOP
Findings:
- Buzzword density is suspiciously high.
- Text contains few concrete anchors such as numbers, versions, commands or file names.
- Text sounds strategic but provides few executable actions.
```

### 5.2 Maven-Koordinaten

Vorgabe für Step 1:

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

Der öffentliche Java-Namespace lautet:

```text
dev.feit.slop4j
```

Der Java Module Name beziehungsweise `Automatic-Module-Name` soll ebenfalls so heißen:

```text
dev.feit.slop4j
```

Damit ist die Library nach außen eindeutig unter `dev.feit.slop4j` positioniert, während das Maven-Artefakt als `slop4j-core` klar macht, dass Step 1 nur die Core-Library liefert. Falls später mehrere Module entstehen, bleibt die Koordinate konsistent: `slop4j-assertj`, `slop4j-maven-plugin`, `slop4j-spring-boot-starter`.

### 5.3 Modulstruktur in Step 1

Für Step 1 reicht ein einziges Maven-Modul:

```text
slop4j/
  pom.xml
  src/
    main/
      java/
        dev/feit/slop4j/
          SlopAnalyzer.java
          SlopAnalyzerBuilder.java
          SlopReport.java
          SlopVerdict.java
          SlopFinding.java
          SlopFindingType.java
          Severity.java
          Language.java
        dev/feit/slop4j/internal/
          SlopContext.java
          TextTokenizer.java
          ScoreMath.java
        dev/feit/slop4j/internal/analysis/
          DefaultSlopAnalyzer.java
          DefaultSlopScorer.java
        dev/feit/slop4j/internal/dictionary/
          ResourceDictionaryLoader.java
          DictionarySet.java
          LanguageDictionary.java
        dev/feit/slop4j/internal/rules/
          SlopRule.java
          BuzzwordRule.java
          VaguePhraseRule.java
          AbstractNounRule.java
          ConcreteAnchorRule.java
          ActionabilityRule.java
          RepetitionRule.java
          OverconfidenceRule.java
      resources/
        dev/feit/slop4j/languages/
          en.yaml
          de.yaml
    test/
      java/
        dev/feit/slop4j/
          SlopAnalyzerTest.java
        dev/feit/slop4j/internal/
          TextTokenizerTest.java
          ResourceDictionaryLoaderTest.java
        dev/feit/slop4j/internal/rules/
          BuzzwordRuleTest.java
          VaguePhraseRuleTest.java
          ConcreteAnchorRuleTest.java
          ActionabilityRuleTest.java
          RepetitionRuleTest.java
          OverconfidenceRuleTest.java
```

Öffentlich sichtbar sollten nur Klassen im Package `dev.feit.slop4j` sein. Alles unter `dev.feit.slop4j.internal` bleibt ausdrücklich nicht-stabile Implementierungsdetails. Der Java Module Name beziehungsweise Automatic-Module-Name sollte ebenfalls `dev.feit.slop4j` lauten.

### 5.4 Öffentliche API

#### 4.4.1 SlopAnalyzer

```java
package dev.feit.slop4j;

public interface SlopAnalyzer {

    SlopReport analyze(String text);

    static SlopReport analyzeText(String text) {
        return builder().build().analyze(text);
    }

    static SlopAnalyzerBuilder builder() {
        return new SlopAnalyzerBuilder();
    }
}
```

Alternative: Die statische Methode könnte auch `analyze(String text)` heißen. Das kollidiert jedoch syntaktisch nicht mit der Instanzmethode, kann aber beim Lesen irritieren. `analyzeText` ist weniger elegant, aber eindeutig. Eine andere saubere Variante wäre eine finale Fassade:

```java
SlopReport report = Slop.analyze(text);
```

Empfehlung für Step 1:

```java
SlopReport report = SlopAnalyzer.builder().build().analyze(text);
```

Eine zusätzliche Convenience-Fassade `Slop` kann später ergänzt werden.

#### 4.4.2 SlopAnalyzerBuilder

Der Builder bleibt bewusst klein.

```java
SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .build();
```

Mögliche API:

```java
public final class SlopAnalyzerBuilder {

    public SlopAnalyzerBuilder language(Language language);

    public SlopAnalyzerBuilder languages(Language first, Language... rest);

    public SlopAnalyzerBuilder languages(Collection<Language> languages);

    public SlopAnalyzerBuilder maxFindingEvidenceLength(int maxFindingEvidenceLength);

    public SlopAnalyzer build();
}
```

Default:

```java
Language.ENGLISH
```

Oder, falls du deutschsprachige Nutzung priorisierst:

```java
Language.ENGLISH, Language.GERMAN
```

Empfehlung: Default nur Englisch. Das ist technisch vorhersehbarer. Wer Deutsch will, aktiviert Deutsch explizit oder nutzt beide Sprachen.

#### 4.4.3 Language

```java
public enum Language {
    ENGLISH("en"),
    GERMAN("de");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
```

Später kann das um `fromCode(String)` erweitert werden.

#### 4.4.4 SlopReport

```java
public record SlopReport(
    double slopScore,
    double buzzwordDensity,
    double vaguePhraseDensity,
    double concretenessScore,
    double actionabilityScore,
    double evidenceScore,
    double repetitionScore,
    double overconfidenceScore,
    SlopVerdict verdict,
    List<SlopFinding> findings
) {
}
```

Alle Scores liegen entweder bei `0.0..1.0` oder beim finalen `slopScore` bei `0.0..100.0`.

Konvention:

- `slopScore`: 0 bis 100
- alle anderen Scores: 0 bis 1

Das sollte in JavaDoc dokumentiert werden.

#### 4.4.5 SlopVerdict

```java
public enum SlopVerdict {
    CLEAN,
    ACCEPTABLY_FLUFFY,
    SLOP_ADJACENT,
    LINKEDIN_READY,
    BOARD_APPROVED_SLOP,
    DANGEROUSLY_USEFUL
}
```

`DANGEROUSLY_USEFUL` ist der satirische Sonderfall für Texte mit sehr niedriger Slop-Wertung und hoher Konkretheit.

#### 4.4.6 SlopFinding

```java
public record SlopFinding(
    SlopFindingType type,
    Severity severity,
    String message,
    String evidence
) {
}
```

`evidence` darf `null` oder leer sein. Alternativ sauberer:

```java
Optional<String> evidence
```

Empfehlung: Kein `Optional` in Records als Feldtyp. Besser `String evidence` mit klarer JavaDoc: Kann `null` sein. Noch besser, wenn du Nulls vermeiden willst:

```java
String evidence
```

und bei fehlender Evidence `""` verwenden.

#### 4.4.7 SlopFindingType

```java
public enum SlopFindingType {
    BUZZWORD_DENSITY,
    VAGUE_PHRASE,
    ABSTRACT_NOUN_DENSITY,
    LOW_EVIDENCE,
    LOW_ACTIONABILITY,
    LOW_CONCRETENESS,
    REPETITION,
    OVERCONFIDENCE
}
```

#### 4.4.8 Severity

```java
public enum Severity {
    INFO,
    WARNING,
    ERROR
}
```

In Step 1 hat `ERROR` noch keine technische Wirkung. Es ist nur semantisch für spätere Nutzung durch Maven Plugin oder CI-Ausgaben nützlich.

### 5.5 Ressourcenbasierte Sprachunterstützung

Die Sprachdaten sollen nicht im Java-Code hartcodiert sein, sondern aus Ressourcen geladen werden.

Ziel:

```text
src/main/resources/dev/feit/slop4j/languages/en.yaml
src/main/resources/dev/feit/slop4j/languages/de.yaml
```

#### 4.5.1 Format der YAML-Dateien

Ein einfaches YAML-Format reicht für Step 1. Die Core-Library nutzt dafür `org.yaml:snakeyaml`.

Beispiel `en.yaml`:

```yaml
buzzwords:
  - agentic
  - autonomous
  - ai-powered
abstractNouns:
  - transformation
  - innovation
vaguePhrases:
  - it depends
  - in many cases
weaselWords:
  - typically
  - generally
activeVerbs:
  - create
  - configure
concreteTerms:
  - java
  - maven
claimMarkers:
  - always
  - guaranteed
```

Beispiel `de.yaml`:

```yaml
buzzwords:
  - agentisch
  - autonom
  - ki-gestützt
abstractNouns:
  - transformation
  - innovation
vaguePhrases:
  - kommt darauf an
  - in vielen fällen
weaselWords:
  - typischerweise
  - generell
activeVerbs:
  - erstellen
  - konfigurieren
concreteTerms:
  - java
  - maven
claimMarkers:
  - immer
  - garantiert
```

#### 4.5.2 Warum YAML?

YAML ist für Step 1 ausreichend strukturiert und bleibt für Wörterlisten gut lesbar:

- weit verbreitete Java-Unterstützung durch SnakeYAML
- Listen müssen nicht in kommagetrennte Strings kodiert werden
- leicht testbar
- einfach zu überschauen
- ausreichend für kleine Wörterlisten

Nachteil: Es entsteht eine kleine Runtime-Abhängigkeit. Für die Pflege zweisprachiger Sprachdaten ist das akzeptiert.

#### 4.5.3 Laden der Dictionaries

Internes Modell:

```java
record DictionarySet(
    Set<String> buzzwords,
    Set<String> abstractNouns,
    List<String> vaguePhrases,
    Set<String> weaselWords,
    Set<String> activeVerbs,
    Set<String> concreteTerms,
    Set<String> claimMarkers
) {
}
```

Loader:

```java
final class ResourceDictionaryLoader {

    DictionarySet load(Language language) {
        // Eine einzelne Sprache laden.
    }

    DictionarySet load(Collection<Language> languages) {
        // Für jede Sprache die passende YAML-Datei laden.
        // Mengen zusammenführen.
        // Alles normalisieren auf lowercase(Locale.ROOT).
        // Duplikate entfernen.
    }
}
```

Wichtig: Wenn mehrere Sprachen ausgewählt werden, werden die Dictionaries zusammengeführt. Es gibt in Step 1 keine automatische Spracherkennung. Der Nutzer entscheidet, welche Sprachen aktiv sind.

Beispiel:

```java
SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .build();
```

Das bedeutet: Der Analyzer erkennt sowohl englische als auch deutsche Buzzwords und Phrasen.

#### 4.5.4 Normalisierung

Alle Ressourcenwerte sollten beim Laden normalisiert werden:

- trimmen
- lowercase mit `Locale.ROOT`
- leere Einträge entfernen
- Duplikate entfernen

Für Sätze und Phrasen gilt ebenfalls Normalisierung.

```java
private static String normalize(String value) {
    return value.strip().toLowerCase(Locale.ROOT);
}
```

Umlaute bleiben erhalten. Keine aggressive Transliteration in Step 1.

### 5.6 Interner Analysefluss

Der Analysefluss sollte stabil und einfach sein:

```text
Input String
  -> null-sicher normalisieren
  -> SlopContext erzeugen
  -> Regeln ausführen
  -> Teilmetriken berechnen
  -> finalen Score berechnen
  -> Verdict bestimmen
  -> SlopReport zurückgeben
```

#### 4.6.1 SlopContext

```java
record SlopContext(
    String originalText,
    String normalizedText,
    List<String> tokens,
    List<String> sentences,
    DictionarySet dictionaries
) {
}
```

`normalizedText` ist lowercase. `originalText` bleibt für Evidence-Ausgaben und spätere Funktionen erhalten.

#### 4.6.2 Tokenizer

Für Step 1 reicht regex-basierte Tokenisierung:

```java
private static final Pattern TOKEN = Pattern.compile("[\\p{L}\\p{N}][\\p{L}\\p{N}\\-']*");
```

Beispiel:

```text
"enterprise-grade AI-powered workflows"
-> enterprise-grade, ai-powered, workflows
```

Satzsplit:

```java
text.split("(?<=[.!?])\\s+")
```

Das ist nicht linguistisch perfekt, aber für README- und LLM-Output-Analyse ausreichend.

### 5.7 Regeln in Step 1

#### 4.7.1 BuzzwordRule

Erkennt überproportional viele Buzzwords.

Metrik:

```text
buzzwordDensity = buzzwordHits / tokenCount
```

Finding-Schwelle:

```text
buzzwordDensity >= 0.08
```

Bei kurzen Texten kann bereits ein einzelnes Buzzword die Dichte stark verzerren. Daher sollte für Findings zusätzlich eine Mindestanzahl gelten:

```text
buzzwordHits >= 3
```

Finding-Beispiel:

```text
Buzzword density is suspiciously high.
Evidence: leverage, agentic, seamless, enterprise-grade
```

#### 4.7.2 VaguePhraseRule

Erkennt unscharfe Formulierungen und typische LLM-Ausweichmuster.

Metrik:

```text
vaguePhraseDensity = vaguePhraseHits / sentenceCount
```

Finding-Schwelle:

```text
vaguePhraseHits >= 2
```

Beispiele:

- it depends
- in many cases
- kann helfen
- kommt darauf an
- moderne Systeme
- depending on your needs

#### 4.7.3 AbstractNounRule

Erkennt Häufung abstrakter Substantive.

Metrik:

```text
abstractNounDensity = abstractNounHits / tokenCount
```

Finding-Schwelle:

```text
abstractNounHits >= 3 && abstractNounDensity >= 0.06
```

Beispiele:

- transformation
- innovation
- enablement
- strategie
- optimierung
- resilienz

#### 4.7.4 ConcreteAnchorRule

Erkennt konkrete Anker, die Slop reduzieren.

Positive Anchors:

- Zahlen
- Versionen
- Dateinamen
- Code-Blöcke
- CLI-Kommandos
- Klassennamen
- Methodennamen
- konkrete Technologien
- messbare Constraints

Patterns:

```java
NUMBER = "\\b\\d+(\\.\\d+)?\\b"
VERSION = "\\bv?\\d+\\.\\d+(\\.\\d+)?\\b"
CODE_BLOCK = "(?s)```.*?```"
FILE_NAME = "\\b[\\w.-]+\\.(java|ts|js|json|yaml|yml|xml|md|sql|sh|properties)\\b"
METHOD_NAME = "\\b[a-z][a-zA-Z0-9]*\\s*\\("
CLASS_NAME = "\\b[A-Z][A-Za-z0-9]+(?:Service|Controller|Repository|Factory|Builder|Config|Configuration|Exception|Client|Mapper)\\b"
CLI_COMMAND = "(?m)^\\s*(mvn|npm|npx|pnpm|yarn|git|docker|kubectl|helm|java|curl)\\b.*$"
```

Metrik:

```text
evidenceScore = min(1.0, weightedAnchorHits / 10.0)
```

Gewichtungsvorschlag:

```text
number: 1
version: 2
code block: 4
file name: 2
method name: 1
class name: 1
cli command: 3
concrete technology term: 1
```

Wenn ein längerer Text kaum konkrete Anker enthält, erzeugt die Regel ein Finding:

```text
Text contains few concrete anchors such as numbers, versions, commands or file names.
```

#### 4.7.5 ActionabilityRule

Erkennt, ob der Text konkrete Handlungen enthält.

Indikatoren:

- aktive Verben aus Ressourcen
- CLI-Kommandos
- nummerierte Schritte
- Imperativ-ähnliche Formulierungen

Für Step 1 reichen aktive Verben und CLI-Kommandos.

Metrik:

```text
actionabilityScore = 0.7 * activeVerbScore + 0.3 * commandScore
```

```text
activeVerbScore = min(1.0, activeVerbHits / 6.0)
commandScore = min(1.0, commandHits / 3.0)
```

Low-Actionability-Finding nur für Texte ab einer Mindestlänge:

```text
tokenCount >= 80 && actionabilityScore < 0.2
```

#### 4.7.6 RepetitionRule

Erkennt Wiederholungen über Token-3-Gramme.

Algorithmus:

```text
Tokens in 3-Gramme schneiden
Häufigkeit je 3-Gramm zählen
Wiederholte 3-Gramme ins Verhältnis zur Tokenanzahl setzen
```

Metrik:

```text
repetitionScore = repeatedNgramCount / tokenCount
```

Finding-Schwelle:

```text
repetitionScore >= 0.08
```

Für sehr kurze Texte deaktivieren:

```text
tokenCount < 30 -> repetitionScore = 0
```

#### 4.7.7 OverconfidenceRule

Erkennt stark selbstsichere Behauptungen ohne Evidenz.

Beispiele:

- always
- never
- guaranteed
- obviously
- clearly
- immer
- nie
- garantiert
- offensichtlich

Metrik:

```text
overconfidenceScore = claimMarkerHits / sentenceCount
```

Finaler Hallucination-Smell beziehungsweise Overconfidence-Penalty:

```text
adjustedOverconfidence = overconfidenceScore * (1.0 - evidenceScore)
```

Das bedeutet: Selbstsichere Aussagen werden weniger stark bestraft, wenn der Text konkrete Belege enthält.

### 5.8 Scoring

Alle Teilmetriken werden auf `0..1` normiert. Der finale Slop Score liegt bei `0..100`.

#### 4.8.1 Sättigungsfunktion

Für Dichten sollte eine Sättigungsfunktion genutzt werden, damit Scores nicht linear explodieren.

```java
static double saturating(double value, double scale) {
    return 1.0 - Math.exp(-value / scale);
}
```

Beispiel:

```java
buzzwordScore = saturating(buzzwordDensity, 0.08);
vagueScore = saturating(vaguePhraseDensity, 1.5);
abstractScore = saturating(abstractNounDensity, 0.07);
```

#### 4.8.2 Length-Gating

Kurze Texte dürfen nicht zu hart bestraft werden, nur weil sie keine Versionen, Befehle oder konkreten Architekturdetails enthalten.

```java
lengthFactor = clamp01(tokenCount / 80.0);
```

Penalties wie `lowEvidence`, `lowActionability` und `lowConcreteness` werden mit `lengthFactor` multipliziert.

#### 4.8.3 Formel für Step 1

Vorschlag:

```text
rawSlop =
    0.24 * buzzwordScore
  + 0.18 * vagueScore
  + 0.14 * abstractScore
  + 0.10 * repetitionScore
  + 0.12 * adjustedOverconfidence
  + lengthFactor * (
        0.08 * lowEvidencePenalty
      + 0.07 * lowActionabilityPenalty
      + 0.07 * lowConcretenessPenalty
    )
```

```text
lowEvidencePenalty = 1.0 - evidenceScore
lowActionabilityPenalty = 1.0 - actionabilityScore
lowConcretenessPenalty = 1.0 - concretenessScore
```

Final:

```text
slopScore = round1(rawSlop * 100)
```

Die Gewichte sind bewusst heuristisch. Wichtig ist, dass sie in Tests anhand repräsentativer Beispiele stabilisiert werden.

### 5.9 Verdict-Mapping

```text
0..19.9     CLEAN
20..39.9    ACCEPTABLY_FLUFFY
40..64.9    SLOP_ADJACENT
65..84.9    LINKEDIN_READY
85..100     BOARD_APPROVED_SLOP
```

Sonderfall:

```text
slopScore < 25 && concretenessScore > 0.7 && actionabilityScore > 0.5
-> DANGEROUSLY_USEFUL
```

Der Sonderfall ist absichtlich satirisch und sollte im README prominent auftauchen.

### 5.10 Teststrategie für Step 1

#### 4.10.1 Unit-Tests für Infrastruktur

- Tokenizer erkennt Wörter mit Bindestrich
- Tokenizer unterstützt Umlaute
- Satzsplit funktioniert ausreichend
- Ressourcenloader lädt `en.yaml`
- Ressourcenloader lädt `de.yaml`
- Ressourcenloader merged mehrere Sprachen
- Ressourcenloader normalisiert auf lowercase
- Ressourcenloader entfernt leere Einträge

#### 4.10.2 Unit-Tests pro Regel

Jede Regel sollte isoliert testbar sein.

Beispiele:

- `BuzzwordRuleTest`
  - hoher Score bei vielen Buzzwords
  - kein Finding bei normalem technischen Text
  - kein false positive bei leerem Text

- `VaguePhraseRuleTest`
  - erkennt englische Phrasen
  - erkennt deutsche Phrasen
  - erkennt mehrere Sprachen bei kombiniertem Analyzer

- `ConcreteAnchorRuleTest`
  - erkennt Versionen
  - erkennt Dateinamen
  - erkennt Code-Blöcke
  - erkennt CLI-Kommandos

- `ActionabilityRuleTest`
  - erkennt aktive Verben
  - erkennt CLI-Kommandos
  - niedriger Score bei rein abstraktem Text

- `RepetitionRuleTest`
  - erkennt wiederholte 3-Gramme
  - ignoriert kurze Texte

- `OverconfidenceRuleTest`
  - erkennt absolute Claim Marker
  - reduziert Wirkung bei hoher Evidence

#### 4.10.3 Golden-Master-ähnliche Tests für Gesamtverhalten

Ein paar exemplarische Texte sollten grob stabile Score-Bereiche haben.

Beispiel 1: maximaler Slop

```text
We leverage agentic AI to unlock seamless enterprise-grade transformation across modern workflows.
Our robust platform enables scalable innovation and future-proof orchestration.
```

Erwartung:

```text
slopScore >= 75
verdict LINKEDIN_READY oder BOARD_APPROVED_SLOP
```

Beispiel 2: technischer Text

```text
Create a Spring Boot service with one POST /payments endpoint.
Persist requests in PostgreSQL 16 using Flyway migrations.
Expose p95 latency and error rate via Micrometer.
```

Erwartung:

```text
slopScore <= 30
verdict CLEAN oder DANGEROUSLY_USEFUL
```

Beispiel 3: deutscher Slop

```text
Unsere KI-gestützte Plattform ermöglicht eine nahtlose und skalierbare Transformation moderner Arbeitsabläufe.
Sie schafft Synergien und verbessert die Effizienz durch ganzheitliche Orchestrierung.
```

Erwartung bei Sprache `GERMAN`:

```text
slopScore >= 65
```

Beispiel 4: deutscher technischer Text

```text
Erstelle einen Spring Boot Service mit einem POST /payments Endpoint.
Speichere Requests in PostgreSQL 16 und versioniere das Schema mit Flyway.
Überwache p95-Latenz und Fehlerrate über Micrometer.
```

Erwartung bei Sprache `GERMAN`:

```text
slopScore <= 35
```

### 5.11 README für Step 1

Das README sollte den satirischen Ton setzen, aber die API ernsthaft dokumentieren.

Minimaler Aufbau:

```text
# slop4j
Enterprise-grade AI Slop Detection for JVM-based Transformation Narratives.

## What it does
## Installation
## Quick Start
## Supported Languages
## Scores
## Verdicts
## Example Reports
## Limitations
```

Wichtiger Disclaimer:

```text
slop4j does not verify factual correctness. It detects textual smells commonly associated with vague, generic or overconfident AI-generated content.
```

Das ist fachlich wichtig. Sonst wirkt die Library so, als könne sie Halluzinationen wirklich erkennen.

### 5.12 Grenzen von Step 1

Step 1 erkennt keine Wahrheit. Die Library kann nicht feststellen, ob eine Aussage sachlich korrekt ist. Sie erkennt nur Textmerkmale.

Bewusst nicht enthalten:

- keine semantische Analyse
- keine automatische Spracheerkennung
- keine echte Halluzinationsprüfung
- keine Quellenprüfung
- keine Grammatikprüfung
- kein komplexes NLP
- keine Embeddings
- kein LLM

Das ist kein Defizit, sondern Teil des Designs.

### 5.13 Definition of Done für Step 1

Step 1 ist fertig, wenn:

- `slop4j-core` per Maven gebaut werden kann
- die öffentliche API stabil genug für README-Beispiele ist
- `en.yaml` und `de.yaml` geladen werden
- Builder eine oder mehrere Sprachen akzeptiert
- Analyzer leere und null-nahe Eingaben robust behandelt
- alle Kernregeln implementiert sind
- Scores plausibel zwischen `0..1` beziehungsweise `0..100` liegen
- Findings verständliche Evidence enthalten
- Unit-Tests für Regeln vorhanden sind
- Gesamtanalyse anhand englischer und deutscher Beispieltexte getestet ist
- README Quick Start funktioniert

## 5. Spätere Ausbaustufen

### 5.1 Step 2: AssertJ-Testmodul

Artefakt:

```text
slop4j-assertj
```

Ziel: Tests auf Slop-Qualität schreiben.

Beispiel:

```java
assertThatSlop(readme)
    .hasSlopScoreBelow(40)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails()
    .doesNotSoundLikeLinkedInPost();
```

Satirische Variante:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady()
    .hasBuzzwordDensityAbove(0.7)
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

Nutzen:

- sehr gute README-Demos
- leicht verständlich für Entwickler
- keine Infrastruktur nötig

### 5.2 Step 3: Maven Plugin

Artefakt:

```text
slop4j-maven-plugin
```

Ziel: README, ADRs und Markdown-Dokumentation scannen.

Beispiel:

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.2.0</version>
    <configuration>
        <maxSlopScore>60</maxSlopScore>
        <languages>
            <language>en</language>
            <language>de</language>
        </languages>
        <includes>
            <include>README.md</include>
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

Mögliche Ausgabe:

```text
[INFO] Slop4J Audit
[WARNING] README.md slopScore=72.8
[WARNING] Detected phrases: AI-powered, seamless developer experience, unlock productivity
[ERROR] Build failed because README.md exceeded maxSlopScore=60
```

Satirische Gegenrichtung:

```xml
<failIfTooConcrete>true</failIfTooConcrete>
<minSlopScore>80</minSlopScore>
```

Ausgabe:

```text
[ERROR] docs/architecture.md is dangerously specific.
[ERROR] Found measurable constraint: p95 latency below 200ms.
[ERROR] This may reduce strategic optionality.
```

### 5.3 Step 4: CLI

Artefakt:

```text
slop4j-cli
```

Beispiel:

```bash
slop4j audit README.md docs/**/*.md --lang en,de --max-score 60
```

Nutzen:

- auch ohne Maven nutzbar
- gut für GitHub Actions, GitLab CI und lokale Pre-Commit-Hooks
- bessere Viralität als reine Java-API

### 5.4 Step 5: Spring Boot Starter

Artefakt:

```text
slop4j-spring-boot-starter
```

Ziel:

- `SlopAnalyzer` als Bean bereitstellen
- Properties für Sprache und Schwellenwerte
- optional Actuator Endpoint

Konfiguration:

```yaml
slop4j:
  enabled: true
  languages:
    - en
    - de
  max-slop-score: 65
```

Nutzung:

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

### 5.5 Step 6: Spring Boot Actuator Endpoint

Endpoint:

```text
GET /actuator/slop
```

Mögliche Antwort:

```json
{
  "application": "payment-service",
  "slopPosture": "MANAGED_BUT_CONCERNING",
  "readmeSlopScore": 68.2,
  "architectureAmbiguity": "HIGH",
  "buzzwordBudgetRemaining": 12
}
```

Das ist vor allem als Enterprise-Satire stark.

### 5.6 Step 7: Annotationen

Artefakt:

```text
slop4j-annotations
```

Beispiele:

```java
@AIPowered
@EnterpriseGrade
@Agentic
public class InvoiceCalculator {
}
```

Späterer Annotation Processor:

```text
[WARNING] InvoiceCalculator is annotated with @Agentic but contains only deterministic arithmetic.
[WARNING] Consider adding YAML, retries, or a remote model call to increase perceived innovation.
```

Weitere Annotationen:

```java
@RequiresThoughtLeadership
@HallucinationResistant
@BoardDeckReady
@PlausiblyDeniable
```

### 5.7 Step 8: Annotation Processor

Artefakt:

```text
slop4j-processor
```

Ziel: Compile-Time-Warnings für absurde oder widersprüchliche Annotationen.

Beispiele:

- Klasse ist `@Agentic`, enthält aber nur statische Methoden
- Klasse ist `@AIPowered`, hat aber keine Netzwerk-, Modell- oder Client-Abhängigkeit
- Klasse heißt `SimpleCsvParser`, ist aber `@EnterpriseGrade`

Das Modul ist satirisch stark, aber technisch etwas aufwendiger. Deshalb nicht früh bauen.

### 5.8 Step 9: Sloppifizierung

Artefakt:

```text
slop4j-transform
```

Ziel: Konkrete technische Aussagen in generischen Enterprise-Slop transformieren.

Beispiel:

```java
String result = SlopTransformer.toBoardDeck("""
    We store user sessions in Redis with a TTL of 30 minutes.
    If Redis is unavailable, login fails fast.
    """);
```

Ausgabe:

```text
The platform leverages a resilient distributed state layer to enable scalable identity continuity across modern access journeys.
```

Weitere Modi:

```java
SlopTransformer.toConsultantSpeak(text);
SlopTransformer.toLinkedInPost(text);
SlopTransformer.removeAccountability(text);
SlopTransformer.toEnterpriseRoadmap(text);
```

Wichtig: Auch diese Funktion kann zunächst ohne AI regelbasiert sein. Gerade das macht sie lustig.

### 5.9 Step 10: Slop-Naming

Artefakt oder Package:

```text
slop4j-naming
```

Beispiele:

```java
SlopNamer.enterpriseNameFor("CSV importer");
```

Ausgabe:

```text
AgenticDelimitedDataIngestionOrchestrator
```

```java
SlopNamer.springBeanNameFor("if else validation");
```

Ausgabe:

```text
AutonomousPolicyEvaluationStrategyEngine
```

Das ist klein, witzig und gut für Social Media oder README-Beispiele.

### 5.10 Step 11: Reports

HTML- und JSON-Reports für Maven Plugin und CLI.

Dateien:

```text
target/slop-report.json
target/slop-report.html
```

JSON-Beispiel:

```json
{
  "summary": {
    "filesScanned": 12,
    "averageSlopScore": 41.2,
    "maxSlopScore": 83.7
  },
  "files": [
    {
      "path": "README.md",
      "slopScore": 72.8,
      "verdict": "LINKEDIN_READY",
      "findings": []
    }
  ]
}
```

### 5.11 Step 12: GitLab CI Integration

Beispiel:

```yaml
slop-audit:
  image: eclipse-temurin:17
  stage: test
  script:
    - ./mvnw -B slop:audit
  artifacts:
    when: always
    paths:
      - target/slop-report.json
      - target/slop-report.html
```

Später eventuell Badge:

```text
Slop Score: 13.7, dangerously useful
```

## 6. Langfristiges Architekturziel

Langfristig kann das Projekt modular aussehen:

```text
slop4j-parent
  slop4j-core
  slop4j-assertj
  slop4j-maven-plugin
  slop4j-cli
  slop4j-spring-boot-starter
  slop4j-actuator
  slop4j-annotations
  slop4j-processor
  slop4j-transform
  slop4j-naming
  slop4j-bom
```

Aber wichtig: Nicht damit starten. Der erste Release sollte klein und nutzbar sein.

## 7. Empfohlene Reihenfolge

1. `slop4j-core`
2. README mit starken Beispielen
3. AssertJ-Modul
4. Maven Plugin
5. CLI
6. Sloppifizierung
7. Spring Boot Starter
8. Actuator Endpoint
9. Annotationen
10. Annotation Processor
11. BOM und Multi-Modul-Aufräumung

## 8. Risiken und Gegenmaßnahmen

### Risiko: Die Scores wirken willkürlich

Gegenmaßnahme:

- Score-Formel dokumentieren
- Beispieltexte mit erwarteten Score-Bereichen testen
- Keine falsche wissenschaftliche Präzision behaupten

### Risiko: Zu viele False Positives

Gegenmaßnahme:

- Findings erst ab Mindesttreffern erzeugen
- Length-Gating nutzen
- konkrete Anker Slop reduzieren lassen
- Sprache explizit konfigurierbar machen

### Risiko: Der Witz überlagert die Nutzbarkeit

Gegenmaßnahme:

- Core API seriös halten
- satirische Verdicts und Messages kontrolliert einsetzen
- README witzig, JavaDoc nüchtern

### Risiko: Zu früher Overengineering-Drang

Gegenmaßnahme:

- Step 1 nur Core Analyzer
- keine Plugins, keine Spring-Integration, keine Annotationen
- Ressourcenmodell einfach halten

## 9. Konkreter Implementierungsplan für Step 1

### Schritt 1.1: Maven-Projekt erstellen

- `pom.xml` mit Java 17
- JUnit 5
- AssertJ nur für Tests
- Maven Surefire Plugin
- optional Maven Enforcer für Java-Version

### Schritt 1.2: Öffentliche API anlegen

- `SlopAnalyzer`
- `SlopAnalyzerBuilder`
- `SlopReport`
- `SlopVerdict`
- `SlopFinding`
- `SlopFindingType`
- `Severity`
- `Language`

Noch keine komplexe Logik.

### Schritt 1.3: Ressourcen-Dateien anlegen

- `en.yaml`
- `de.yaml`

Zunächst kleine, kuratierte Listen. Nicht zu viele Begriffe. Lieber präzise starten und später erweitern.

### Schritt 1.4: ResourceDictionaryLoader implementieren

- eine Sprache laden
- mehrere Sprachen mergen
- Normalisierung
- Fehler bei fehlender Ressource sauber behandeln
- Unit-Tests für `en`, `de`, `en+de`

### Schritt 1.5: Tokenizer und SlopContext implementieren

- Tokenisierung
- Satzsplit
- Normalisierung
- Null- und Blank-Handling

### Schritt 1.6: Regeln implementieren

In dieser Reihenfolge:

1. `BuzzwordRule`
2. `VaguePhraseRule`
3. `ConcreteAnchorRule`
4. `ActionabilityRule`
5. `AbstractNounRule`
6. `RepetitionRule`
7. `OverconfidenceRule`

Jede Regel zuerst mit Unit-Test.

### Schritt 1.7: Scorer implementieren

- Teilmetriken aggregieren
- Score-Formel anwenden
- Verdict bestimmen
- Rundung zentralisieren

### Schritt 1.8: DefaultSlopAnalyzer implementieren

- Context erstellen
- Regeln ausführen
- Scorer aufrufen
- Report erzeugen

### Schritt 1.9: Integrationstests schreiben

- englischer Slop-Text
- englischer technischer Text
- deutscher Slop-Text
- deutscher technischer Text
- leerer Text
- sehr kurzer Text
- gemischtsprachiger Text mit `ENGLISH + GERMAN`

### Schritt 1.10: README schreiben

Fokus:

- Installation
- Quick Start
- Sprachkonfiguration
- Beispielreports
- Limitations
- Roadmap

## 10. Empfehlung für den ersten öffentlichen Eindruck

Der erste öffentliche Eindruck sollte nicht „noch eine halbfertige Joke-Lib“ sein. Besser:

- kleine API
- gute README
- deterministische Beispiele
- witzige Verdicts
- echte Tests
- keine unnötigen Module

Der Kern sollte seriös genug sein, dass Entwickler ihn in einem echten Test oder Demo-Projekt verwenden könnten. Die Satire entsteht aus Naming, Verdicts und Messages, nicht aus schlechter Implementierung.

## 11. Kurzfassung der Step-1-API

```java
SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .build();

SlopReport report = analyzer.analyze(text);

if (report.slopScore() > 60) {
    System.out.println("Too much slop: " + report.verdict());
    report.findings().forEach(System.out::println);
}
```

## 12. Kurzfassung der Roadmap

```text
0.1.0  Core Analyzer
0.2.0  AssertJ Assertions
0.3.0  Maven Plugin
0.4.0  CLI
0.5.0  Sloppifizierung
0.6.0  Spring Boot Starter
0.7.0  Actuator Endpoint
0.8.0  Annotationen
0.9.0  Annotation Processor
1.0.0  Stabilisierung, BOM, Maven Central Release
```
