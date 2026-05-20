# slop4j Step 2: Spezifikation für das AssertJ-Testmodul

## 1. Zielbild

Step 2 ergänzt `slop4j` um ein separates AssertJ-Modul, mit dem Entwickler
Texte in Tests gegen die Analyseergebnisse aus `slop4j-core` prüfen können.
Das Modul soll keine neue Analyse-Logik enthalten, sondern ausschließlich eine
fluent Test-API über `SlopAnalyzer` und `SlopReport` bereitstellen.

Das Artefakt heißt:

```text
slop4j-assertj
```

Der öffentliche Java-Namespace lautet:

```text
dev.feit.slop4j.assertj
```

Der automatische Modulname lautet:

```text
dev.feit.slop4j.assertj
```

Step 2 soll die Core-Library in Tests unmittelbar verwendbar machen:

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails()
    .doesNotSoundLikeLinkedInPost();
```

## 2. Produktrolle

`slop4j-assertj` ist das erste Integrationsmodul nach dem Core Analyzer. Es
zeigt, dass die Analyse nicht nur als Demo-Ausgabe, sondern als echte
Testbedingung nutzbar ist. Der primäre Anwendungsfall sind Unit- und
Dokumentationstests für README-Dateien, ADRs, Produkttexte, API-Beschreibungen
und generierte technische Dokumentation.

Das Modul soll bewusst ernsthaft wirken. Die API darf satirische
domänenspezifische Methoden enthalten, aber JavaDoc, Fehlermeldungen und
README-Dokumentation müssen professionell und trocken bleiben. Die
Satire entsteht aus Methodennamen wie `isBoardDeckReady()` oder
`maximizesPlausibleDeniability()`, nicht aus albernen Kommentaren oder
unpräzisem Verhalten.

## 3. Scope

### 3.1 Enthalten in Step 2

- neues Maven-Modul `slop4j-assertj`
- öffentliche AssertJ-Einstiegsklasse `SlopAssertions`
- AssertJ-Assert-Klasse für rohe Texte
- AssertJ-Assert-Klasse für vorhandene `SlopReport`-Instanzen
- Konfiguration über Sprachen und optional eigenen `SlopAnalyzer`
- Score-Assertions für alle numerischen Felder aus `SlopReport`
- Verdict-Assertions
- Finding-Assertions
- Convenience-Assertions für typische Core-Signale
- satirische Convenience-Assertions als fachlich klar definierte Aliase
- Unit-Tests für Erfolg und Fehlerausgaben
- README- und README_DE-Erweiterung mit synchronen Step-2-Beispielen
- BOM-Erweiterung für das neue Modul

### 3.2 Nicht enthalten in Step 2

- kein Maven Plugin
- keine Dateisuche oder Glob-Verarbeitung
- keine CLI
- keine automatische Markdown-Extraktion
- keine neuen Slop-Regeln
- keine Änderung der Score-Formel im Core
- keine zusätzlichen Runtime-Abhängigkeiten im Core
- keine automatische Spracheerkennung
- keine Soft-Assertion-Spezialintegration über AssertJ hinaus

## 4. Maven- und Modulstruktur

Die Projektstruktur soll um ein Modul erweitert werden:

```text
slop4j/
  pom.xml
  slop4j-bom/
    pom.xml
  slop4j-core/
    pom.xml
  slop4j-assertj/
    pom.xml
    src/
      main/
        java/
          dev/feit/slop4j/assertj/
            SlopAssertions.java
            SlopTextAssert.java
            SlopReportAssert.java
            package-info.java
      test/
        java/
          dev/feit/slop4j/assertj/
            SlopAssertionsTest.java
            SlopTextAssertTest.java
            SlopReportAssertTest.java
```

Das Root-`pom.xml` nimmt `slop4j-assertj` in `<modules>` auf. Das
`slop4j-bom`-Modul muss `slop4j-assertj` zusätzlich zu `slop4j-core`
versionieren, damit Verbraucher beide Artefakte konsistent importieren können.

Empfohlene Koordinate:

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-assertj</artifactId>
    <version>${revision}</version>
    <scope>test</scope>
</dependency>
```

`slop4j-assertj` hängt von `slop4j-core` und `assertj-core` ab. Da das Modul
selbst eine Test-Hilfsbibliothek ist, ist `assertj-core` für dieses Artefakt
eine reguläre Compile-Abhängigkeit. Für Konsumenten wird das Artefakt
typischerweise mit `test`-Scope eingebunden.

JSpecify bleibt wie im Core mit `provided`-Scope erlaubt. Das Modul sollte
`@NullMarked` in `package-info.java` verwenden.

## 5. Öffentliche API

### 5.1 Einstiegsklasse `SlopAssertions`

`SlopAssertions` ist die einzige statische Einstiegsklasse. Sie ist final,
hat einen privaten Konstruktor und enthält nur statische Factory-Methoden.

```java
package dev.feit.slop4j.assertj;

import dev.feit.slop4j.Language;
import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import java.util.Collection;

public final class SlopAssertions {

    public static SlopTextAssert assertThatSlop(String actual);

    public static SlopTextAssert assertThatSlop(String actual, Language first, Language... rest);

    public static SlopTextAssert assertThatSlop(String actual, Collection<Language> languages);

    public static SlopTextAssert assertThatSlop(String actual, SlopAnalyzer analyzer);

    public static SlopReportAssert assertThat(SlopReport actual);
}
```

`assertThatSlop(String)` nutzt `SlopAnalyzer.builder().build()` und damit die
Default-Sprache aus dem Core. Für mehrsprachige Tests sollen die
Language-Overloads genutzt werden:

```java
assertThatSlop(germanReadme, Language.GERMAN)
    .hasSlopScoreBelow(45.0);
```

Der Analyzer-Overload ist für Tests gedacht, die eine zentral konfigurierte
Analyzer-Instanz wiederverwenden:

```java
SlopAnalyzer analyzer = SlopAnalyzer.builder()
    .languages(Language.ENGLISH, Language.GERMAN)
    .maxFindingEvidenceLength(80)
    .build();

assertThatSlop(text, analyzer)
    .hasNoFindingOfType(SlopFindingType.OVERCONFIDENCE);
```

`assertThat(SlopReport)` erlaubt Assertions auf bereits berechneten Reports,
ohne den Text erneut zu analysieren:

```java
SlopReport report = analyzer.analyze(text);

SlopAssertions.assertThat(report)
    .hasVerdict(SlopVerdict.DANGEROUSLY_USEFUL)
    .hasEvidenceScoreAbove(0.6);
```

### 5.2 `SlopTextAssert`

`SlopTextAssert` repräsentiert einen zu analysierenden Text. Intern berechnet
sie genau einmal einen `SlopReport` und delegiert die meisten Assertions an
`SlopReportAssert`. Die Analyse soll lazy erfolgen: Der Report wird erst beim
ersten Zugriff erzeugt und danach in der Assert-Instanz wiederverwendet.

Vorgesehene Signatur:

```java
public final class SlopTextAssert extends AbstractAssert<SlopTextAssert, String> {

    public SlopTextAssert hasSlopScoreBelow(double maximumExclusive);

    public SlopTextAssert hasSlopScoreAtMost(double maximumInclusive);

    public SlopTextAssert hasSlopScoreAbove(double minimumExclusive);

    public SlopTextAssert hasSlopScoreAtLeast(double minimumInclusive);

    public SlopTextAssert hasBuzzwordDensityBelow(double maximumExclusive);

    public SlopTextAssert hasBuzzwordDensityAbove(double minimumExclusive);

    public SlopTextAssert hasVaguePhraseDensityBelow(double maximumExclusive);

    public SlopTextAssert hasConcretenessScoreAbove(double minimumExclusive);

    public SlopTextAssert hasActionabilityScoreAbove(double minimumExclusive);

    public SlopTextAssert hasEvidenceScoreAbove(double minimumExclusive);

    public SlopTextAssert hasRepetitionScoreBelow(double maximumExclusive);

    public SlopTextAssert hasOverconfidenceScoreBelow(double maximumExclusive);

    public SlopTextAssert hasVerdict(SlopVerdict expected);

    public SlopTextAssert doesNotHaveVerdict(SlopVerdict unexpected);

    public SlopTextAssert hasFindingOfType(SlopFindingType expectedType);

    public SlopTextAssert hasNoFindingOfType(SlopFindingType unexpectedType);

    public SlopTextAssert hasFindingWithSeverity(Severity expectedSeverity);

    public SlopTextAssert containsConcreteDetails();

    public SlopTextAssert lacksConcreteDetails();

    public SlopTextAssert isActionable();

    public SlopTextAssert isInsufficientlyActionable();

    public SlopTextAssert doesNotSoundLikeLinkedInPost();

    public SlopTextAssert soundsLikeLinkedInPost();

    public SlopTextAssert isBoardDeckReady();

    public SlopTextAssert containsNoImplementationDetails();

    public SlopTextAssert maximizesPlausibleDeniability();

    public SlopReport actualReport();
}
```

`actualReport()` gibt den berechneten `SlopReport` zurück. Die Methode ist
nützlich, wenn ein Test nach einer fluent Assertion noch Detailwerte prüfen
oder ausgeben möchte. Sie darf keine Assertion auslösen, außer die Analyse
selbst kann wegen ungültiger Konfiguration nicht erzeugt werden.

### 5.3 `SlopReportAssert`

`SlopReportAssert` prüft vorhandene Reports und enthält dieselbe fachliche
Assertion-Semantik wie `SlopTextAssert`, aber ohne Textanalyse.

Vorgesehene Signatur:

```java
public final class SlopReportAssert extends AbstractAssert<SlopReportAssert, SlopReport> {

    public SlopReportAssert hasSlopScoreBelow(double maximumExclusive);

    public SlopReportAssert hasSlopScoreAtMost(double maximumInclusive);

    public SlopReportAssert hasSlopScoreAbove(double minimumExclusive);

    public SlopReportAssert hasSlopScoreAtLeast(double minimumInclusive);

    public SlopReportAssert hasBuzzwordDensityBelow(double maximumExclusive);

    public SlopReportAssert hasBuzzwordDensityAbove(double minimumExclusive);

    public SlopReportAssert hasVaguePhraseDensityBelow(double maximumExclusive);

    public SlopReportAssert hasConcretenessScoreAbove(double minimumExclusive);

    public SlopReportAssert hasActionabilityScoreAbove(double minimumExclusive);

    public SlopReportAssert hasEvidenceScoreAbove(double minimumExclusive);

    public SlopReportAssert hasRepetitionScoreBelow(double maximumExclusive);

    public SlopReportAssert hasOverconfidenceScoreBelow(double maximumExclusive);

    public SlopReportAssert hasVerdict(SlopVerdict expected);

    public SlopReportAssert doesNotHaveVerdict(SlopVerdict unexpected);

    public SlopReportAssert hasFindingOfType(SlopFindingType expectedType);

    public SlopReportAssert hasNoFindingOfType(SlopFindingType unexpectedType);

    public SlopReportAssert hasFindingWithSeverity(Severity expectedSeverity);

    public SlopReportAssert containsConcreteDetails();

    public SlopReportAssert lacksConcreteDetails();

    public SlopReportAssert isActionable();

    public SlopReportAssert isInsufficientlyActionable();

    public SlopReportAssert doesNotSoundLikeLinkedInPost();

    public SlopReportAssert soundsLikeLinkedInPost();

    public SlopReportAssert isBoardDeckReady();

    public SlopReportAssert containsNoImplementationDetails();

    public SlopReportAssert maximizesPlausibleDeniability();
}
```

Die Methodennamen in `SlopTextAssert` und `SlopReportAssert` sollen bewusst
parallel bleiben, damit Tests später leicht von Text- auf Report-Assertions
umgestellt werden können.

## 6. Assertion-Semantik

### 6.1 Numerische Score-Assertions

Alle Score-Assertions verwenden die vorhandenen Werte aus `SlopReport`.
Es wird keine Neuberechnung und keine zusätzliche Rundung vorgenommen.

Scorebereiche:

- `slopScore`: `0.0..100.0`
- alle anderen Scores: `0.0..1.0`

Ungültige Schwellenwerte sollen mit `IllegalArgumentException` abgelehnt
werden, bevor eine Assertion geprüft wird:

- `hasSlopScore...`: erlaubt `0.0..100.0`
- alle anderen Score-Assertions: erlaubt `0.0..1.0`
- `NaN`, positive Unendlichkeit und negative Unendlichkeit sind ungültig

Exklusive Methoden verwenden strikte Vergleiche:

```text
hasSlopScoreBelow(40.0)     -> actual < 40.0
hasSlopScoreAbove(40.0)     -> actual > 40.0
```

Inklusive Methoden verwenden inklusive Vergleiche:

```text
hasSlopScoreAtMost(40.0)    -> actual <= 40.0
hasSlopScoreAtLeast(40.0)   -> actual >= 40.0
```

Für Scores außer `slopScore` reichen zunächst die Methoden, die in typischen
Tests am meisten gebraucht werden. Es ist nicht erforderlich, für jeden Score
sofort alle vier Vergleichsvarianten anzubieten. Die API muss aber konsistent
erweiterbar bleiben.

### 6.2 Verdict-Assertions

`hasVerdict(expected)` prüft exakte Gleichheit mit `SlopReport.verdict()`.
`doesNotHaveVerdict(unexpected)` schlägt fehl, wenn der Report genau dieses
Verdict enthält.

`null` ist für erwartete Verdicts ungültig und soll mit
`NullPointerException` oder `IllegalArgumentException` abgelehnt werden. Die
konkrete Exception-Art soll innerhalb des Moduls konsistent sein; bevorzugt
ist `Objects.requireNonNull`.

### 6.3 Finding-Assertions

`hasFindingOfType(type)` schlägt fehl, wenn kein Finding mit exakt diesem
`SlopFindingType` existiert.

`hasNoFindingOfType(type)` schlägt fehl, wenn mindestens ein Finding mit exakt
diesem Typ existiert.

`hasFindingWithSeverity(severity)` schlägt fehl, wenn kein Finding mit exakt
dieser `Severity` existiert.

Die Fehlerausgabe soll bei Finding-Fehlern die vorhandenen Findings kompakt
auflisten. Pro Finding reichen Typ, Severity und Message. Evidence darf
angezeigt werden, wenn sie nicht leer ist.

### 6.4 Convenience-Assertions

Convenience-Assertions sind feste, dokumentierte Aliase über Score-, Verdict-
oder Finding-Bedingungen. Sie dürfen keine zusätzliche Analyse erfinden.

Empfohlene Definitionen:

```text
containsConcreteDetails()
  -> concretenessScore > 0.5 oder evidenceScore > 0.5

lacksConcreteDetails()
  -> concretenessScore < 0.25 und evidenceScore < 0.25

isActionable()
  -> actionabilityScore > 0.5

isInsufficientlyActionable()
  -> actionabilityScore < 0.25 oder Finding LOW_ACTIONABILITY vorhanden

doesNotSoundLikeLinkedInPost()
  -> verdict ist weder LINKEDIN_READY noch BOARD_APPROVED_SLOP

soundsLikeLinkedInPost()
  -> verdict ist LINKEDIN_READY oder BOARD_APPROVED_SLOP

isBoardDeckReady()
  -> verdict ist BOARD_APPROVED_SLOP oder slopScore >= 85.0

containsNoImplementationDetails()
  -> evidenceScore < 0.2 und concretenessScore < 0.3

maximizesPlausibleDeniability()
  -> slopScore >= 75.0
     und actionabilityScore < 0.3
     und evidenceScore < 0.3
```

Diese Schwellenwerte sind bewusst heuristisch, müssen aber in JavaDoc und Tests
stabil beschrieben werden. Wenn Step 1 seine Score-Semantik später ändert,
sollen diese Aliase nur dann angepasst werden, wenn die dokumentierte Bedeutung
dadurch klarer wird.

## 7. Fehlerausgaben

Fehlerausgaben sind ein Kernnutzen des Moduls. Sie sollen nicht nur sagen, dass
eine Assertion fehlgeschlagen ist, sondern die relevanten Analysewerte zeigen.

Beispiel für `hasSlopScoreBelow(40.0)`:

```text
Expected slop score to be below 40.0, but was 72.8.

Report:
  verdict: LINKEDIN_READY
  slopScore: 72.8
  buzzwordDensity: 0.18
  vaguePhraseDensity: 0.50
  concretenessScore: 0.10
  actionabilityScore: 0.12
  evidenceScore: 0.00
  repetitionScore: 0.00
  overconfidenceScore: 0.25

Findings:
  - BUZZWORD_DENSITY WARNING: Buzzword density is suspiciously high.
  - LOW_EVIDENCE WARNING: Text contains few concrete anchors such as numbers, versions, commands or file names.
```

Beispiel für `hasNoFindingOfType(OVERCONFIDENCE)`:

```text
Expected no finding of type OVERCONFIDENCE, but found:
  OVERCONFIDENCE WARNING: Text contains strongly confident claims with limited evidence.
```

Die tatsächlichen Messages müssen zu den Core-Messages passen. Tests sollen
nicht auf komplette lange Messages fixiert werden, sondern auf die relevanten
Bestandteile: erwartete Schwelle, tatsächlicher Wert, Verdict und Finding-Typ.

## 8. Null- und Blank-Verhalten

`assertThatSlop(null)` soll nicht schon in der Factory-Methode fehlschlagen,
sondern das Verhalten des Core Analyzers nutzen. Wenn der Core `null` robust
als leeren Text behandelt, soll das AssertJ-Modul dieses Verhalten erhalten.

`assertThat((SlopReport) null)` beziehungsweise
`SlopAssertions.assertThat(null)` soll wie übliche AssertJ-Assertions erst bei
einer fachlichen Assertion mit einer klaren Fehlermeldung scheitern. Interne
Hilfsmethoden müssen deshalb `isNotNull()` aufrufen, bevor sie Report-Felder
lesen.

Leere und blanke Texte sollen analysierbar bleiben. Sie können niedrige Scores
oder fehlende Findings erzeugen, dürfen aber keine AssertJ-spezifische
Sonderbehandlung bekommen.

## 9. JavaDoc und Sprache

Alle öffentlichen Klassen und Methoden erhalten knappe JavaDoc. Der Ton ist
trocken und technisch. JavaDoc soll erklären:

- welcher `SlopReport`-Wert geprüft wird
- ob ein Vergleich exklusiv oder inklusiv ist
- welcher Scorebereich für Schwellenwerte gültig ist
- welche konkreten Bedingungen Convenience-Assertions verwenden

JavaDoc soll keine Witze, keine Meta-Kommentare und keine überlange
Produktbeschreibung enthalten.

## 10. README-Erweiterung

`README.md` und `README_DE.md` müssen synchron erweitert werden. Die englische
README bleibt die primäre Fassung, die deutsche README spiegelt den Inhalt.

Minimaler neuer Abschnitt:

```text
## AssertJ Assertions
```

Inhalt:

- Maven-Testdependency für `slop4j-assertj`
- statischer Import von `SlopAssertions.assertThatSlop`
- ein seriöses Quality-Gate-Beispiel
- ein satirisch benanntes, aber fachlich deterministisches Beispiel
- Hinweis, dass das Modul auf `slop4j-core` basiert und keine neue Analyse
  ausführt

Englisches Beispiel:

```java
import static dev.feit.slop4j.assertj.SlopAssertions.assertThatSlop;

assertThatSlop(readme, Language.ENGLISH, Language.GERMAN)
    .hasSlopScoreBelow(40.0)
    .hasActionabilityScoreAbove(0.5)
    .containsConcreteDetails();
```

Satirisches Beispiel:

```java
assertThatSlop(strategyDeck)
    .isBoardDeckReady()
    .containsNoImplementationDetails()
    .maximizesPlausibleDeniability();
```

Der Text muss klarstellen, dass diese Methoden deterministische Aliase über
Score- und Finding-Bedingungen sind.

## 11. Teststrategie

### 11.1 API-Factory-Tests

`SlopAssertionsTest` prüft:

- `assertThatSlop(String)` erzeugt eine `SlopTextAssert`
- `assertThatSlop(String, Language...)` akzeptiert Deutsch und Englisch
- `assertThatSlop(String, Collection<Language>)` lehnt leere Collections über
  den Core-Builder ab
- `assertThatSlop(String, SlopAnalyzer)` verwendet den übergebenen Analyzer
- `assertThat(SlopReport)` erzeugt eine `SlopReportAssert`

### 11.2 Text-Assert-Tests

`SlopTextAssertTest` nutzt repräsentative Texte aus Step 1:

- technischer Text besteht `hasSlopScoreBelow(40.0)`
- Slop-Text schlägt bei `hasSlopScoreBelow(40.0)` fehl
- deutscher Slop-Text wird mit `Language.GERMAN` korrekt erkannt
- `actualReport()` gibt denselben Report über mehrere Aufrufe zurück
- Convenience-Assertions delegieren konsistent auf Report-Werte

### 11.3 Report-Assert-Tests

`SlopReportAssertTest` soll überwiegend synthetische `SlopReport`-Instanzen
verwenden. Dadurch bleiben Tests unabhängig von Score-Tuning im Core.

Zu prüfen:

- alle numerischen Assertion-Erfolge
- alle numerischen Assertion-Fehler mit relevanten Message-Bestandteilen
- ungültige Schwellenwerte für `slopScore`
- ungültige Schwellenwerte für `0.0..1.0`-Scores
- Verdict-Erfolg und Verdict-Fehler
- Finding-Typ-Erfolg und Finding-Typ-Fehler
- Severity-Erfolg und Severity-Fehler
- null Report schlägt mit klarer AssertJ-Fehlermeldung fehl

### 11.4 README-Beispieltests

Falls die Beispiele als Java-Testklassen abbildbar sind, soll mindestens ein
Test die README-Quick-Start-Nutzung für das AssertJ-Modul kompilieren und
ausführen. Alternativ können die Beispiele in einer kleinen
`slop4j-examples`-Erweiterung untergebracht werden, wenn das besser zur
Projektstruktur passt.

## 12. Akzeptanzkriterien

Step 2 ist fertig, wenn:

- `slop4j-assertj` als neues Maven-Modul gebaut wird
- das Root-Projekt `slop4j-assertj` in `<modules>` enthält
- `slop4j-bom` das neue Artefakt verwaltet
- `slop4j-assertj` den automatischen Modulnamen `dev.feit.slop4j.assertj`
  setzt
- `SlopAssertions.assertThatSlop(...)` für Texte funktioniert
- `SlopAssertions.assertThat(SlopReport)` für vorhandene Reports funktioniert
- Score-, Verdict- und Finding-Assertions implementiert und getestet sind
- Convenience-Assertions dokumentiert und getestet sind
- Fehlerausgaben die relevanten Analysewerte enthalten
- öffentliche Klassen JSpecify-nullmarkiert sind
- `README.md` und `README_DE.md` synchron um AssertJ-Nutzung erweitert sind
- `mvn clean test` erfolgreich läuft
- `mvn spotless:check` erfolgreich läuft

## 13. Festgelegte Umsetzungsentscheidungen

Die folgenden Entscheidungen gelten für die spätere Umsetzung, sofern sich beim
Implementieren kein harter technischer Widerspruch ergibt:

- `SlopAssertions.assertThat(SlopReport)` wird angeboten, obwohl der Name die
  AssertJ-Konvention aufgreift. Für bereits berechnete Reports liest sich diese
  Form am natürlichsten.
- `assertThatSlop(null)` bleibt Core-kompatibel und übergibt den Wert an den
  Analyzer. Wenn der Core `null` als leeren Text behandelt, gilt dasselbe für
  das AssertJ-Modul.
- Convenience-Assertions gelten als stabile öffentliche API. Ihre Semantik ist
  heuristisch, aber in JavaDoc und Tests konkret dokumentiert.
- `slop4j-examples` wird in Step 2 nicht erweitert. README-Beispiele und Tests
  im neuen Modul reichen für die erste AssertJ-Integration aus.

## 14. Verhältnis zu späteren Steps

Step 2 darf keine Funktionalität vorwegnehmen, die für Maven Plugin oder CLI
vorgesehen ist. Insbesondere soll das AssertJ-Modul keine Dateien selbst lesen
und keine Include-/Exclude-Logik einführen. Solche Funktionen gehören in
Step 3 oder Step 4.

Das Modul soll aber die Begriffe und Schwellenwerte vorbereiten, die später in
Maven Plugin und CLI wiederverwendet werden können:

- maximale Slop-Scores
- minimale Actionability
- Mindestmaß an Concrete Details
- erlaubte oder unerwünschte Verdicts
- verbotene Finding-Typen

Dadurch kann Step 3 später dieselbe fachliche Sprache nutzen, ohne die
AssertJ-API als technische Abhängigkeit verwenden zu müssen.
