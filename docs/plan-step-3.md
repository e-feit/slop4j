# Maven Plugin Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build `slop4j-maven-plugin`, a Maven plugin with an `audit` goal that scans README, ADR, and Markdown documentation files with `slop4j-core` and optionally fails the build when configured slop thresholds are exceeded.

**Architecture:** The plugin is a separate Maven module with `maven-plugin` packaging. `SlopAuditMojo` owns Maven parameter binding and delegates all business logic to small internal services: language parsing, file scanning, per-file auditing, threshold decisions, and console reporting. The module depends on `slop4j-core` for analysis and does not introduce new slop scoring logic.

**Tech Stack:** Java 17, Maven Plugin API, Maven Plugin Annotations, Maven Plugin Plugin, JUnit 5, AssertJ, JSpecify, existing parent Spotless and JaCoCo setup.

---

## 1. Scope

Step 3 adds a Maven plugin module:

```text
slop4j-maven-plugin
```

The plugin exposes one goal:

```text
slop4j:audit
```

The goal scans text files selected by include and exclude glob patterns,
analyzes each file with `SlopAnalyzer`, logs a compact per-file result, and
fails the build when the configured policy is violated.

The first version intentionally stays console-only. JSON and HTML report files
belong to the later reports step from `docs/plan.md` and must not be added in
Step 3.

## 2. User-Facing Configuration

The primary usage in a consumer project should look like this:

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

The satirical inverse quality gate from `docs/plan.md` is also in scope:

```xml
<configuration>
    <failIfTooConcrete>true</failIfTooConcrete>
    <minSlopScore>80.0</minSlopScore>
</configuration>
```

This mode fails when documentation is below the configured minimum slop score.
It is intentionally deterministic: it uses only `SlopReport.slopScore()` and
selected high-evidence findings from the core report.

## 3. Maven Parameters

`SlopAuditMojo` exposes these parameters:

| Parameter | Type | Default | Meaning |
| --- | --- | --- | --- |
| `maxSlopScore` | `double` | `60.0` | Maximum allowed `SlopReport.slopScore()` when `failOnSlop` is enabled. |
| `minSlopScore` | `double` | `80.0` | Minimum required `SlopReport.slopScore()` when `failIfTooConcrete` is enabled. |
| `failOnSlop` | `boolean` | `true` | Fails the build when at least one scanned file has `slopScore > maxSlopScore`. |
| `failIfTooConcrete` | `boolean` | `false` | Fails the build when at least one scanned file has `slopScore < minSlopScore`. |
| `languages` | `List<String>` | `["en"]` | Language codes passed to `SlopAnalyzer`. Supported values: `en`, `de`, `english`, `german`. |
| `includes` | `List<String>` | `["README.md", "README_DE.md", "docs/**/*.md", "adr/**/*.md"]` | Project-relative glob patterns to scan. |
| `excludes` | `List<String>` | `["target/**", ".git/**"]` | Project-relative glob patterns ignored after include matching. |
| `skip` | `boolean` | `false` | Skips plugin execution. User property: `slop4j.skip`. |
| `failIfNoFiles` | `boolean` | `false` | Fails when no files match the configured includes and excludes. |
| `maxFindingsPerFile` | `int` | `5` | Maximum number of findings printed per file. |
| `maxFindingEvidenceLength` | `int` | `120` | Passed into `SlopAnalyzer.builder().maxFindingEvidenceLength(...)`. |

Validation rules:

- `maxSlopScore` must be in `0.0..100.0`.
- `minSlopScore` must be in `0.0..100.0`.
- `maxFindingsPerFile` must be `>= 0`.
- `maxFindingEvidenceLength` must be `>= 0`.
- `languages` must contain at least one supported language.
- `includes` must contain at least one non-blank pattern after trimming.
- `excludes` may be empty.
- `failOnSlop` and `failIfTooConcrete` may both be enabled. In that case, both policies are evaluated and all violations are reported before the build fails.

## 4. Console Output Contract

Successful audit with findings but no build failure:

```text
[INFO] Slop4J Audit
[INFO] Scanned 3 file(s).
[INFO] README.md slopScore=31.2 verdict=ACCEPTABLY_FLUFFY findings=1
[INFO] docs/architecture.md slopScore=18.4 verdict=DANGEROUSLY_USEFUL findings=0
[INFO] docs/roadmap.md slopScore=54.7 verdict=SLOP_ADJACENT findings=3
[INFO] Slop4J Audit completed without policy violations.
```

Failure from `failOnSlop`:

```text
[INFO] Slop4J Audit
[INFO] Scanned 1 file(s).
[WARNING] README.md slopScore=72.8 verdict=LINKEDIN_READY findings=2
[WARNING]   BUZZWORD_DENSITY WARNING: Buzzword density is suspiciously high.
[WARNING]   LOW_EVIDENCE WARNING: Text contains few concrete anchors such as numbers, versions, commands or file names.
[ERROR] Build failed because 1 file(s) exceeded maxSlopScore=60.0.
```

Failure from `failIfTooConcrete`:

```text
[INFO] Slop4J Audit
[INFO] Scanned 1 file(s).
[ERROR] docs/architecture.md is dangerously specific.
[ERROR]   slopScore=13.7 is below minSlopScore=80.0.
[ERROR]   verdict=DANGEROUSLY_USEFUL
[ERROR]   This may reduce strategic optionality.
[ERROR] Build failed because 1 file(s) were below minSlopScore=80.0.
```

No files found with `failIfNoFiles=false`:

```text
[INFO] Slop4J Audit
[WARNING] No files matched the configured slop audit includes.
```

No files found with `failIfNoFiles=true`:

```text
[INFO] Slop4J Audit
[ERROR] No files matched the configured slop audit includes.
```

## 5. File Structure

Create or modify these files:

```text
pom.xml
slop4j-bom/pom.xml
README.md
README_DE.md
slop4j-maven-plugin/pom.xml
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/SlopAuditMojo.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/package-info.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditConfiguration.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditDecision.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditSummary.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/ConsoleReporter.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/FileSlopResult.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/LanguageParser.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/MarkdownFileScanner.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/SlopFileAuditor.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/ThresholdPolicy.java
slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/package-info.java
slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/SlopAuditMojoTest.java
slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/AuditConfigurationTest.java
slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/ConsoleReporterTest.java
slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/LanguageParserTest.java
slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/MarkdownFileScannerTest.java
slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/SlopFileAuditorTest.java
slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/ThresholdPolicyTest.java
```

Ownership boundaries:

- `SlopAuditMojo`: Maven parameter binding, validation handoff, orchestration, and `MojoExecutionException` conversion.
- `AuditConfiguration`: immutable, validated runtime configuration created from Mojo parameters.
- `LanguageParser`: maps Maven language strings to `Language` enum values.
- `MarkdownFileScanner`: resolves project-relative glob patterns to regular files.
- `SlopFileAuditor`: reads files as UTF-8 and calls `SlopAnalyzer`.
- `ThresholdPolicy`: decides whether a file violates `maxSlopScore` or `minSlopScore`.
- `AuditSummary`: aggregates file results and policy decisions.
- `ConsoleReporter`: produces all user-facing Maven log lines.

## 6. Public and Internal API Details

### 6.1 `SlopAuditMojo`

Package:

```text
dev.feit.slop4j.maven.plugin
```

Required annotations:

```java
@Mojo(name = "audit", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true, requiresProject = true)
```

Fields:

```java
@Parameter(defaultValue = "${project.basedir}", readonly = true, required = true)
private File basedir;

@Parameter(defaultValue = "60.0")
private double maxSlopScore;

@Parameter(defaultValue = "80.0")
private double minSlopScore;

@Parameter(defaultValue = "true")
private boolean failOnSlop;

@Parameter(defaultValue = "false")
private boolean failIfTooConcrete;

@Parameter
private List<String> languages;

@Parameter
private List<String> includes;

@Parameter
private List<String> excludes;

@Parameter(property = "slop4j.skip", defaultValue = "false")
private boolean skip;

@Parameter(defaultValue = "false")
private boolean failIfNoFiles;

@Parameter(defaultValue = "5")
private int maxFindingsPerFile;

@Parameter(defaultValue = "120")
private int maxFindingEvidenceLength;
```

Defaults for list parameters are applied in `AuditConfiguration` rather than
through annotation strings, because list defaults are easier to test and less
fragile when Maven injects `null`.

### 6.2 `AuditConfiguration`

Record fields:

```java
public record AuditConfiguration(
    Path baseDirectory,
    double maxSlopScore,
    double minSlopScore,
    boolean failOnSlop,
    boolean failIfTooConcrete,
    List<Language> languages,
    List<String> includes,
    List<String> excludes,
    boolean failIfNoFiles,
    int maxFindingsPerFile,
    int maxFindingEvidenceLength
)
```

Factory method:

```java
public static AuditConfiguration create(
    File basedir,
    double maxSlopScore,
    double minSlopScore,
    boolean failOnSlop,
    boolean failIfTooConcrete,
    List<String> languages,
    List<String> includes,
    List<String> excludes,
    boolean failIfNoFiles,
    int maxFindingsPerFile,
    int maxFindingEvidenceLength
)
```

Default lists:

```java
languages: ["en"]
includes: ["README.md", "README_DE.md", "docs/**/*.md", "adr/**/*.md"]
excludes: ["target/**", ".git/**"]
```

### 6.3 `FileSlopResult`

Record fields:

```java
public record FileSlopResult(Path relativePath, SlopReport report)
```

`relativePath` is always normalized to use forward slashes in display output.
Internally it remains a `Path` so tests can compare paths without string
parsing.

### 6.4 `AuditDecision`

Record fields:

```java
public record AuditDecision(
    FileSlopResult result,
    boolean exceedsMaximumSlop,
    boolean belowMinimumSlop
)
```

Helper methods:

```java
public boolean violatesPolicy()
public String displayPath()
```

`violatesPolicy()` returns `exceedsMaximumSlop || belowMinimumSlop`.

### 6.5 `AuditSummary`

Record fields:

```java
public record AuditSummary(List<FileSlopResult> results, List<AuditDecision> decisions)
```

Helper methods:

```java
public int scannedFileCount()
public long maximumSlopViolationCount()
public long minimumSlopViolationCount()
public boolean hasPolicyViolations()
```

### 6.6 `ThresholdPolicy`

Constructor:

```java
public ThresholdPolicy(AuditConfiguration configuration)
```

Method:

```java
public AuditDecision evaluate(FileSlopResult result)
```

Semantics:

```text
exceedsMaximumSlop = configuration.failOnSlop()
    && result.report().slopScore() > configuration.maxSlopScore()

belowMinimumSlop = configuration.failIfTooConcrete()
    && result.report().slopScore() < configuration.minSlopScore()
```

### 6.7 `LanguageParser`

Method:

```java
List<Language> parse(List<String> values)
```

Accepted values after trim and lowercase:

```text
en       -> Language.ENGLISH
english  -> Language.ENGLISH
de       -> Language.GERMAN
german   -> Language.GERMAN
deutsch  -> Language.GERMAN
```

Blank values are ignored. If all values are blank, throw:

```text
IllegalArgumentException("At least one language must be configured.")
```

Unsupported values throw:

```text
IllegalArgumentException("Unsupported slop4j language: <value>")
```

### 6.8 `MarkdownFileScanner`

Constructor:

```java
public MarkdownFileScanner(Path baseDirectory)
```

Method:

```java
public List<Path> scan(List<String> includes, List<String> excludes) throws IOException
```

Semantics:

- Walk `baseDirectory` recursively with `Files.walk(baseDirectory)`.
- Consider only regular files.
- Convert each file to a project-relative path.
- A file is selected when it matches at least one include pattern and no exclude pattern.
- Matching uses project-relative forward-slash paths.
- Patterns are Maven-style globs:
  - `README.md`
  - `docs/*.md`
  - `docs/**/*.md`
  - `adr/**/*.md`
- Result order is lexicographic by normalized relative path.
- Returned paths are absolute file paths.

Implementation detail:

Use a small glob-to-regex matcher instead of relying directly on
`FileSystem.getPathMatcher("glob:...")`, because forward-slash project
relative paths must behave consistently on all operating systems.

Required matching examples:

```text
README.md matches README.md
README_DE.md matches README_DE.md
docs/plan.md matches docs/**/*.md
docs/architecture/context.md matches docs/**/*.md
docs/architecture/context.md does not match docs/*.md
target/generated.md is excluded by target/**
```

### 6.9 `SlopFileAuditor`

Constructor:

```java
public SlopFileAuditor(AuditConfiguration configuration)
```

Method:

```java
public List<FileSlopResult> audit(List<Path> absoluteFiles) throws IOException
```

Semantics:

- Build one `SlopAnalyzer` for the whole audit with configured languages and `maxFindingEvidenceLength`.
- Read every file as UTF-8.
- Analyze the full file content, including Markdown syntax.
- Compute relative path from `configuration.baseDirectory()`.
- Return results in the same order as the input file list.

Markdown stripping is deliberately not part of Step 3. The core analyzer should
see the same text a developer sees in the file.

### 6.10 `ConsoleReporter`

Constructor:

```java
public ConsoleReporter(Log log)
```

Methods:

```java
public void reportSkipped()
public void reportNoFiles(boolean failIfNoFiles)
public void reportSummary(AuditSummary summary, AuditConfiguration configuration)
```

The reporter is the only class that formats Maven log output. It must not
decide policy. It receives decisions from `AuditSummary`.

## 7. Task Plan

### Task 1: Add Maven Plugin Module Skeleton

**Files:**
- Modify: `pom.xml`
- Modify: `slop4j-bom/pom.xml`
- Create: `slop4j-maven-plugin/pom.xml`
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/package-info.java`
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/package-info.java`

- [ ] **Step 1: Add plugin tooling properties to the root POM**

Add these properties beside the existing version properties:

```xml
<maven.version>3.9.9</maven.version>
<maven-plugin-tools.version>3.15.1</maven-plugin-tools.version>
```

- [ ] **Step 2: Add the new module to the root POM**

Add the module after `slop4j-assertj`:

```xml
<module>slop4j-maven-plugin</module>
```

- [ ] **Step 3: Add plugin dependencies to root dependency management**

Add these dependencies in `pom.xml` dependency management:

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>${project.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven-plugin-api</artifactId>
    <version>${maven.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.maven.plugin-tools</groupId>
    <artifactId>maven-plugin-annotations</artifactId>
    <version>${maven-plugin-tools.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.maven.plugin-testing</groupId>
    <artifactId>maven-plugin-testing-harness</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 4: Add the plugin plugin to root plugin management**

Add this plugin to `pluginManagement`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-plugin-plugin</artifactId>
    <version>${maven-plugin-tools.version}</version>
</plugin>
```

- [ ] **Step 5: Add `slop4j-maven-plugin` to the BOM**

Add this dependency to `slop4j-bom/pom.xml` under the Slop4J module entries:

```xml
<dependency>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>${project.version}</version>
</dependency>
```

- [ ] **Step 6: Create `slop4j-maven-plugin/pom.xml`**

Use this module POM:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>dev.feit</groupId>
        <artifactId>slop4j</artifactId>
        <version>${revision}</version>
    </parent>

    <artifactId>slop4j-maven-plugin</artifactId>
    <packaging>maven-plugin</packaging>

    <name>slop4j-maven-plugin</name>
    <description>Maven plugin for deterministic slop auditing of project documentation.</description>

    <dependencies>
        <dependency>
            <groupId>dev.feit</groupId>
            <artifactId>slop4j-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.maven</groupId>
            <artifactId>maven-plugin-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.maven.plugin-tools</groupId>
            <artifactId>maven-plugin-annotations</artifactId>
        </dependency>
        <dependency>
            <groupId>org.jspecify</groupId>
            <artifactId>jspecify</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-plugin-plugin</artifactId>
                <configuration>
                    <goalPrefix>slop4j</goalPrefix>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Automatic-Module-Name>dev.feit.slop4j.maven.plugin</Automatic-Module-Name>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 7: Create package nullability descriptors**

Create `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/package-info.java`:

```java
@NullMarked
package dev.feit.slop4j.maven.plugin;

import org.jspecify.annotations.NullMarked;
```

Create `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/package-info.java`:

```java
@NullMarked
package dev.feit.slop4j.maven.plugin.internal;

import org.jspecify.annotations.NullMarked;
```

- [ ] **Step 8: Run module compile and verify descriptor generation fails for missing Mojo**

Run:

```bash
mvn -pl slop4j-maven-plugin -am test
```

Expected: FAIL because no Mojo class exists yet or no plugin descriptor can be generated.

- [ ] **Step 9: Commit module skeleton**

```bash
git add pom.xml slop4j-bom/pom.xml slop4j-maven-plugin/pom.xml slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/package-info.java slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/package-info.java
git commit -m "build: add slop4j maven plugin module"
```

### Task 2: Implement Language Parsing

**Files:**
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/LanguageParser.java`
- Create: `slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/LanguageParserTest.java`

- [ ] **Step 1: Write failing tests for language parsing**

Create `LanguageParserTest.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.feit.slop4j.Language;
import java.util.List;
import org.junit.jupiter.api.Test;

class LanguageParserTest {

	@Test
	void parsesEnglishAliases() {
		assertThat(new LanguageParser().parse(List.of("en", "english")))
				.containsExactly(Language.ENGLISH, Language.ENGLISH);
	}

	@Test
	void parsesGermanAliases() {
		assertThat(new LanguageParser().parse(List.of("de", "german", "deutsch")))
				.containsExactly(Language.GERMAN, Language.GERMAN, Language.GERMAN);
	}

	@Test
	void trimsAndIgnoresBlankValues() {
		assertThat(new LanguageParser().parse(List.of(" en ", "", "  ", "DE")))
				.containsExactly(Language.ENGLISH, Language.GERMAN);
	}

	@Test
	void rejectsUnsupportedLanguage() {
		assertThatThrownBy(() -> new LanguageParser().parse(List.of("fr")))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Unsupported slop4j language: fr");
	}

	@Test
	void rejectsEmptyEffectiveLanguageList() {
		assertThatThrownBy(() -> new LanguageParser().parse(List.of("", " ")))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("At least one language must be configured.");
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=LanguageParserTest test
```

Expected: FAIL because `LanguageParser` does not exist.

- [ ] **Step 3: Implement `LanguageParser`**

Create `LanguageParser.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.Language;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

final class LanguageParser {

	List<Language> parse(List<String> values) {
		Objects.requireNonNull(values, "values");
		List<Language> languages = new ArrayList<>();
		for (String value : values) {
			if (value == null || value.isBlank()) {
				continue;
			}
			languages.add(parseOne(value.strip()));
		}
		if (languages.isEmpty()) {
			throw new IllegalArgumentException("At least one language must be configured.");
		}
		return List.copyOf(languages);
	}

	private static Language parseOne(String value) {
		return switch (value.toLowerCase(Locale.ROOT)) {
			case "en", "english" -> Language.ENGLISH;
			case "de", "german", "deutsch" -> Language.GERMAN;
			default -> throw new IllegalArgumentException("Unsupported slop4j language: " + value);
		};
	}
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=LanguageParserTest test
```

Expected: PASS.

- [ ] **Step 5: Commit language parser**

```bash
git add slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/LanguageParser.java slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/LanguageParserTest.java
git commit -m "feat: parse slop audit languages"
```

### Task 3: Implement Audit Configuration

**Files:**
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditConfiguration.java`
- Create: `slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/AuditConfigurationTest.java`

- [ ] **Step 1: Write failing configuration tests**

Create `AuditConfigurationTest.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.feit.slop4j.Language;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditConfigurationTest {

	@Test
	void appliesDefaultsForNullLists() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null,
				null, null, false, 5, 120);

		assertThat(configuration.languages()).containsExactly(Language.ENGLISH);
		assertThat(configuration.includes()).containsExactly("README.md", "README_DE.md", "docs/**/*.md",
				"adr/**/*.md");
		assertThat(configuration.excludes()).containsExactly("target/**", ".git/**");
	}

	@Test
	void trimsIncludesAndExcludes() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false,
				List.of("de"), List.of(" README.md ", "docs/**/*.md"), List.of(" target/** "), false, 5, 120);

		assertThat(configuration.languages()).containsExactly(Language.GERMAN);
		assertThat(configuration.includes()).containsExactly("README.md", "docs/**/*.md");
		assertThat(configuration.excludes()).containsExactly("target/**");
	}

	@Test
	void rejectsInvalidMaxSlopScore() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 100.1, 80.0, true, false, null, null,
				null, false, 5, 120))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("maxSlopScore must be between 0.0 and 100.0.");
	}

	@Test
	void rejectsInvalidMinSlopScore() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 60.0, -0.1, true, false, null, null,
				null, false, 5, 120))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("minSlopScore must be between 0.0 and 100.0.");
	}

	@Test
	void rejectsEmptyIncludes() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null,
				List.of("", " "), null, false, 5, 120))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("At least one include pattern must be configured.");
	}

	@Test
	void rejectsNegativeFindingLimit() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null, null,
				null, false, -1, 120))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("maxFindingsPerFile must not be negative.");
	}

	@Test
	void rejectsNegativeEvidenceLength() {
		assertThatThrownBy(() -> AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null, null,
				null, false, 5, -1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("maxFindingEvidenceLength must not be negative.");
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=AuditConfigurationTest test
```

Expected: FAIL because `AuditConfiguration` does not exist.

- [ ] **Step 3: Implement `AuditConfiguration`**

Create `AuditConfiguration.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.Language;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public record AuditConfiguration(Path baseDirectory, double maxSlopScore, double minSlopScore, boolean failOnSlop,
		boolean failIfTooConcrete, List<Language> languages, List<String> includes, List<String> excludes,
		boolean failIfNoFiles, int maxFindingsPerFile, int maxFindingEvidenceLength) {

	private static final List<String> DEFAULT_LANGUAGES = List.of("en");
	private static final List<String> DEFAULT_INCLUDES = List.of("README.md", "README_DE.md", "docs/**/*.md",
			"adr/**/*.md");
	private static final List<String> DEFAULT_EXCLUDES = List.of("target/**", ".git/**");

	public AuditConfiguration {
		Objects.requireNonNull(baseDirectory, "baseDirectory");
		languages = List.copyOf(Objects.requireNonNull(languages, "languages"));
		includes = List.copyOf(Objects.requireNonNull(includes, "includes"));
		excludes = List.copyOf(Objects.requireNonNull(excludes, "excludes"));
	}

	public static AuditConfiguration create(File basedir, double maxSlopScore, double minSlopScore, boolean failOnSlop,
			boolean failIfTooConcrete, List<String> languages, List<String> includes, List<String> excludes,
			boolean failIfNoFiles, int maxFindingsPerFile, int maxFindingEvidenceLength) {
		if (!isScore(maxSlopScore)) {
			throw new IllegalArgumentException("maxSlopScore must be between 0.0 and 100.0.");
		}
		if (!isScore(minSlopScore)) {
			throw new IllegalArgumentException("minSlopScore must be between 0.0 and 100.0.");
		}
		if (maxFindingsPerFile < 0) {
			throw new IllegalArgumentException("maxFindingsPerFile must not be negative.");
		}
		if (maxFindingEvidenceLength < 0) {
			throw new IllegalArgumentException("maxFindingEvidenceLength must not be negative.");
		}

		List<Language> parsedLanguages = new LanguageParser().parse(defaultIfNull(languages, DEFAULT_LANGUAGES));
		List<String> normalizedIncludes = normalizePatterns(defaultIfNull(includes, DEFAULT_INCLUDES));
		if (normalizedIncludes.isEmpty()) {
			throw new IllegalArgumentException("At least one include pattern must be configured.");
		}
		List<String> normalizedExcludes = normalizePatterns(defaultIfNull(excludes, DEFAULT_EXCLUDES));

		return new AuditConfiguration(Objects.requireNonNull(basedir, "basedir").toPath().toAbsolutePath().normalize(),
				maxSlopScore, minSlopScore, failOnSlop, failIfTooConcrete, parsedLanguages, normalizedIncludes,
				normalizedExcludes, failIfNoFiles, maxFindingsPerFile, maxFindingEvidenceLength);
	}

	private static boolean isScore(double value) {
		return !Double.isNaN(value) && !Double.isInfinite(value) && value >= 0.0 && value <= 100.0;
	}

	private static List<String> defaultIfNull(List<String> values, List<String> defaults) {
		return values == null ? defaults : values;
	}

	private static List<String> normalizePatterns(List<String> patterns) {
		return patterns.stream()
				.filter(Objects::nonNull)
				.map(String::strip)
				.filter(pattern -> !pattern.isBlank())
				.toList();
	}
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=AuditConfigurationTest test
```

Expected: PASS.

- [ ] **Step 5: Commit configuration**

```bash
git add slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditConfiguration.java slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/AuditConfigurationTest.java
git commit -m "feat: configure slop audit plugin"
```

### Task 4: Implement Markdown File Scanner

**Files:**
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/MarkdownFileScanner.java`
- Create: `slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/MarkdownFileScannerTest.java`

- [ ] **Step 1: Write failing scanner tests**

Create `MarkdownFileScannerTest.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarkdownFileScannerTest {

	@TempDir
	Path tempDir;

	@Test
	void scansRootAndNestedMarkdownFiles() throws Exception {
		write("README.md");
		write("README_DE.md");
		write("docs/plan.md");
		write("docs/architecture/context.md");
		write("src/main/java/Example.java");

		List<Path> files = new MarkdownFileScanner(tempDir)
				.scan(List.of("README.md", "README_DE.md", "docs/**/*.md"), List.of("target/**", ".git/**"));

		assertThat(relative(files)).containsExactly("README.md", "README_DE.md", "docs/architecture/context.md",
				"docs/plan.md");
	}

	@Test
	void excludesConfiguredPaths() throws Exception {
		write("README.md");
		write("target/generated.md");
		write(".git/ignored.md");

		List<Path> files = new MarkdownFileScanner(tempDir).scan(List.of("**/*.md", "README.md"),
				List.of("target/**", ".git/**"));

		assertThat(relative(files)).containsExactly("README.md");
	}

	@Test
	void singleStarDoesNotMatchNestedDirectories() throws Exception {
		write("docs/root.md");
		write("docs/nested/deep.md");

		List<Path> files = new MarkdownFileScanner(tempDir).scan(List.of("docs/*.md"), List.of());

		assertThat(relative(files)).containsExactly("docs/root.md");
	}

	private void write(String relativePath) throws Exception {
		Path path = tempDir.resolve(relativePath);
		Files.createDirectories(path.getParent() == null ? tempDir : path.getParent());
		Files.writeString(path, "content");
	}

	private List<String> relative(List<Path> files) {
		return files.stream()
				.map(tempDir::relativize)
				.map(Path::toString)
				.map(path -> path.replace('\\', '/'))
				.toList();
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=MarkdownFileScannerTest test
```

Expected: FAIL because `MarkdownFileScanner` does not exist.

- [ ] **Step 3: Implement scanner with project-relative glob matching**

Create `MarkdownFileScanner.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class MarkdownFileScanner {

	private final Path baseDirectory;

	public MarkdownFileScanner(Path baseDirectory) {
		this.baseDirectory = Objects.requireNonNull(baseDirectory, "baseDirectory").toAbsolutePath().normalize();
	}

	public List<Path> scan(List<String> includes, List<String> excludes) throws IOException {
		List<Pattern> includePatterns = compile(includes);
		List<Pattern> excludePatterns = compile(excludes);
		try (var stream = Files.walk(baseDirectory)) {
			return stream.filter(Files::isRegularFile)
					.filter(path -> isIncluded(path, includePatterns, excludePatterns))
					.sorted(Comparator.comparing(this::displayPath))
					.toList();
		}
	}

	private boolean isIncluded(Path path, List<Pattern> includes, List<Pattern> excludes) {
		String relativePath = displayPath(path);
		boolean included = includes.stream().anyMatch(pattern -> pattern.matcher(relativePath).matches());
		boolean excluded = excludes.stream().anyMatch(pattern -> pattern.matcher(relativePath).matches());
		return included && !excluded;
	}

	private String displayPath(Path path) {
		return baseDirectory.relativize(path.toAbsolutePath().normalize()).toString().replace('\\', '/');
	}

	private static List<Pattern> compile(List<String> patterns) {
		return patterns.stream().map(MarkdownFileScanner::toRegex).map(Pattern::compile).toList();
	}

	private static String toRegex(String glob) {
		StringBuilder regex = new StringBuilder("^");
		for (int i = 0; i < glob.length(); i++) {
			char current = glob.charAt(i);
			if (current == '*') {
				boolean doubleStar = i + 1 < glob.length() && glob.charAt(i + 1) == '*';
				if (doubleStar) {
					boolean followedBySlash = i + 2 < glob.length() && glob.charAt(i + 2) == '/';
					if (followedBySlash) {
						regex.append("(?:.*/)?");
						i += 2;
					} else {
						regex.append(".*");
						i++;
					}
				} else {
					regex.append("[^/]*");
				}
			} else if (current == '?') {
				regex.append("[^/]");
			} else {
				if ("\\.[]{}()+-^$|".indexOf(current) >= 0) {
					regex.append('\\');
				}
				regex.append(current);
			}
		}
		regex.append('$');
		return regex.toString();
	}
}
```

- [ ] **Step 4: Run scanner tests**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=MarkdownFileScannerTest test
```

Expected: PASS.

- [ ] **Step 5: Commit scanner**

```bash
git add slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/MarkdownFileScanner.java slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/MarkdownFileScannerTest.java
git commit -m "feat: scan documentation files for slop audit"
```

### Task 5: Implement File Auditing

**Files:**
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/FileSlopResult.java`
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/SlopFileAuditor.java`
- Create: `slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/SlopFileAuditorTest.java`

- [ ] **Step 1: Write failing auditor tests**

Create `SlopFileAuditorTest.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlopFileAuditorTest {

	@TempDir
	Path tempDir;

	@Test
	void auditsFilesWithConfiguredLanguages() throws Exception {
		Path readme = write("README.md",
				"Unsere KI-gestützte Plattform ermöglicht eine nahtlose skalierbare Transformation.");
		AuditConfiguration configuration = AuditConfiguration.create(tempDir.toFile(), 60.0, 80.0, true, false,
				List.of("de"), List.of("README.md"), List.of(), false, 5, 120);

		List<FileSlopResult> results = new SlopFileAuditor(configuration).audit(List.of(readme));

		assertThat(results).hasSize(1);
		assertThat(results.get(0).relativePath().toString().replace('\\', '/')).isEqualTo("README.md");
		assertThat(results.get(0).report().slopScore()).isGreaterThan(0.0);
	}

	private Path write(String relativePath, String content) throws Exception {
		Path path = tempDir.resolve(relativePath);
		Files.createDirectories(path.getParent() == null ? tempDir : path.getParent());
		Files.writeString(path, content);
		return path;
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=SlopFileAuditorTest test
```

Expected: FAIL because `SlopFileAuditor` and `FileSlopResult` do not exist.

- [ ] **Step 3: Implement result record**

Create `FileSlopResult.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.SlopReport;
import java.nio.file.Path;
import java.util.Objects;

public record FileSlopResult(Path relativePath, SlopReport report) {

	public FileSlopResult {
		Objects.requireNonNull(relativePath, "relativePath");
		Objects.requireNonNull(report, "report");
	}

	public String displayPath() {
		return relativePath.toString().replace('\\', '/');
	}
}
```

- [ ] **Step 4: Implement auditor**

Create `SlopFileAuditor.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.SlopAnalyzer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SlopFileAuditor {

	private final AuditConfiguration configuration;

	public SlopFileAuditor(AuditConfiguration configuration) {
		this.configuration = Objects.requireNonNull(configuration, "configuration");
	}

	public List<FileSlopResult> audit(List<Path> absoluteFiles) throws IOException {
		SlopAnalyzer analyzer = SlopAnalyzer.builder()
				.languages(configuration.languages())
				.maxFindingEvidenceLength(configuration.maxFindingEvidenceLength())
				.build();
		List<FileSlopResult> results = new ArrayList<>();
		for (Path file : absoluteFiles) {
			String content = Files.readString(file, StandardCharsets.UTF_8);
			Path relativePath = configuration.baseDirectory().relativize(file.toAbsolutePath().normalize());
			results.add(new FileSlopResult(relativePath, analyzer.analyze(content)));
		}
		return List.copyOf(results);
	}
}
```

- [ ] **Step 5: Run auditor tests**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=SlopFileAuditorTest test
```

Expected: PASS.

- [ ] **Step 6: Commit auditor**

```bash
git add slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/FileSlopResult.java slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/SlopFileAuditor.java slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/SlopFileAuditorTest.java
git commit -m "feat: audit documentation files with slop analyzer"
```

### Task 6: Implement Threshold Decisions and Summary

**Files:**
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditDecision.java`
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditSummary.java`
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/ThresholdPolicy.java`
- Create: `slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/ThresholdPolicyTest.java`

- [ ] **Step 1: Write failing threshold tests**

Create `ThresholdPolicyTest.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThresholdPolicyTest {

	@Test
	void detectsMaximumSlopViolationWhenEnabled() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null,
				null, null, false, 5, 120);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(resultWithScore(72.8));

		assertThat(decision.exceedsMaximumSlop()).isTrue();
		assertThat(decision.belowMinimumSlop()).isFalse();
		assertThat(decision.violatesPolicy()).isTrue();
	}

	@Test
	void ignoresMaximumSlopViolationWhenDisabled() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, false, false, null,
				null, null, false, 5, 120);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(resultWithScore(72.8));

		assertThat(decision.violatesPolicy()).isFalse();
	}

	@Test
	void detectsTooConcreteViolationWhenEnabled() {
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, false, true, null,
				null, null, false, 5, 120);

		AuditDecision decision = new ThresholdPolicy(configuration).evaluate(resultWithScore(13.7));

		assertThat(decision.exceedsMaximumSlop()).isFalse();
		assertThat(decision.belowMinimumSlop()).isTrue();
		assertThat(decision.violatesPolicy()).isTrue();
	}

	@Test
	void summarizesViolationCounts() {
		AuditSummary summary = new AuditSummary(List.of(resultWithScore(90.0), resultWithScore(10.0)),
				List.of(new AuditDecision(resultWithScore(90.0), true, false),
						new AuditDecision(resultWithScore(10.0), false, true)));

		assertThat(summary.scannedFileCount()).isEqualTo(2);
		assertThat(summary.maximumSlopViolationCount()).isEqualTo(1);
		assertThat(summary.minimumSlopViolationCount()).isEqualTo(1);
		assertThat(summary.hasPolicyViolations()).isTrue();
	}

	private static FileSlopResult resultWithScore(double slopScore) {
		return new FileSlopResult(Path.of("README.md"), new SlopReport(slopScore, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, SlopVerdict.CLEAN, List.<SlopFinding>of()));
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=ThresholdPolicyTest test
```

Expected: FAIL because threshold classes do not exist.

- [ ] **Step 3: Implement `AuditDecision`**

Create `AuditDecision.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import java.util.Objects;

public record AuditDecision(FileSlopResult result, boolean exceedsMaximumSlop, boolean belowMinimumSlop) {

	public AuditDecision {
		Objects.requireNonNull(result, "result");
	}

	public boolean violatesPolicy() {
		return exceedsMaximumSlop || belowMinimumSlop;
	}

	public String displayPath() {
		return result.displayPath();
	}
}
```

- [ ] **Step 4: Implement `AuditSummary`**

Create `AuditSummary.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import java.util.List;
import java.util.Objects;

public record AuditSummary(List<FileSlopResult> results, List<AuditDecision> decisions) {

	public AuditSummary {
		results = List.copyOf(Objects.requireNonNull(results, "results"));
		decisions = List.copyOf(Objects.requireNonNull(decisions, "decisions"));
	}

	public int scannedFileCount() {
		return results.size();
	}

	public long maximumSlopViolationCount() {
		return decisions.stream().filter(AuditDecision::exceedsMaximumSlop).count();
	}

	public long minimumSlopViolationCount() {
		return decisions.stream().filter(AuditDecision::belowMinimumSlop).count();
	}

	public boolean hasPolicyViolations() {
		return decisions.stream().anyMatch(AuditDecision::violatesPolicy);
	}
}
```

- [ ] **Step 5: Implement `ThresholdPolicy`**

Create `ThresholdPolicy.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import java.util.Objects;

public final class ThresholdPolicy {

	private final AuditConfiguration configuration;

	public ThresholdPolicy(AuditConfiguration configuration) {
		this.configuration = Objects.requireNonNull(configuration, "configuration");
	}

	public AuditDecision evaluate(FileSlopResult result) {
		double score = result.report().slopScore();
		boolean exceedsMaximumSlop = configuration.failOnSlop() && score > configuration.maxSlopScore();
		boolean belowMinimumSlop = configuration.failIfTooConcrete() && score < configuration.minSlopScore();
		return new AuditDecision(result, exceedsMaximumSlop, belowMinimumSlop);
	}
}
```

- [ ] **Step 6: Run threshold tests**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=ThresholdPolicyTest test
```

Expected: PASS.

- [ ] **Step 7: Commit threshold policy**

```bash
git add slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditDecision.java slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/AuditSummary.java slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/ThresholdPolicy.java slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/ThresholdPolicyTest.java
git commit -m "feat: evaluate slop audit thresholds"
```

### Task 7: Implement Console Reporter

**Files:**
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/ConsoleReporter.java`
- Create: `slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/ConsoleReporterTest.java`

- [ ] **Step 1: Write failing reporter tests**

Create `ConsoleReporterTest.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.SlopReport;
import dev.feit.slop4j.SlopVerdict;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

class ConsoleReporterTest {

	@Test
	void reportsMaximumSlopViolationWithFindings() {
		CapturingLog log = new CapturingLog();
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, true, false, null,
				null, null, false, 1, 120);
		FileSlopResult result = new FileSlopResult(Path.of("README.md"),
				new SlopReport(72.8, 0.2, 0.0, 0.1, 0.1, 0.0, 0.0, 0.0, SlopVerdict.LINKEDIN_READY,
						List.of(new SlopFinding(SlopFindingType.BUZZWORD_DENSITY, Severity.WARNING,
								"Buzzword density is suspiciously high.", "agentic"))));
		AuditSummary summary = new AuditSummary(List.of(result), List.of(new AuditDecision(result, true, false)));

		new ConsoleReporter(log).reportSummary(summary, configuration);

		assertThat(log.info).contains("Slop4J Audit", "Scanned 1 file(s).");
		assertThat(log.warn).anyMatch(line -> line.contains("README.md slopScore=72.8 verdict=LINKEDIN_READY"));
		assertThat(log.warn).anyMatch(line -> line.contains("BUZZWORD_DENSITY WARNING"));
		assertThat(log.error).contains("Build failed because 1 file(s) exceeded maxSlopScore=60.0.");
	}

	@Test
	void reportsTooConcreteViolation() {
		CapturingLog log = new CapturingLog();
		AuditConfiguration configuration = AuditConfiguration.create(new File("."), 60.0, 80.0, false, true, null,
				null, null, false, 5, 120);
		FileSlopResult result = new FileSlopResult(Path.of("docs/architecture.md"),
				new SlopReport(13.7, 0.0, 0.0, 0.9, 0.8, 0.9, 0.0, 0.0, SlopVerdict.DANGEROUSLY_USEFUL,
						List.of()));
		AuditSummary summary = new AuditSummary(List.of(result), List.of(new AuditDecision(result, false, true)));

		new ConsoleReporter(log).reportSummary(summary, configuration);

		assertThat(log.error).anyMatch(line -> line.contains("docs/architecture.md is dangerously specific."));
		assertThat(log.error).anyMatch(line -> line.contains("slopScore=13.7 is below minSlopScore=80.0."));
	}

	private static final class CapturingLog implements Log {

		private final List<String> debug = new ArrayList<>();
		private final List<String> info = new ArrayList<>();
		private final List<String> warn = new ArrayList<>();
		private final List<String> error = new ArrayList<>();

		@Override
		public boolean isDebugEnabled() {
			return true;
		}

		@Override
		public void debug(CharSequence content) {
			debug.add(content.toString());
		}

		@Override
		public void debug(CharSequence content, Throwable error) {
			debug.add(content.toString());
		}

		@Override
		public void debug(Throwable error) {
			debug.add(error.toString());
		}

		@Override
		public boolean isInfoEnabled() {
			return true;
		}

		@Override
		public void info(CharSequence content) {
			info.add(content.toString());
		}

		@Override
		public void info(CharSequence content, Throwable error) {
			info.add(content.toString());
		}

		@Override
		public void info(Throwable error) {
			info.add(error.toString());
		}

		@Override
		public boolean isWarnEnabled() {
			return true;
		}

		@Override
		public void warn(CharSequence content) {
			warn.add(content.toString());
		}

		@Override
		public void warn(CharSequence content, Throwable error) {
			warn.add(content.toString());
		}

		@Override
		public void warn(Throwable error) {
			warn.add(error.toString());
		}

		@Override
		public boolean isErrorEnabled() {
			return true;
		}

		@Override
		public void error(CharSequence content) {
			error.add(content.toString());
		}

		@Override
		public void error(CharSequence content, Throwable error) {
			this.error.add(content.toString());
		}

		@Override
		public void error(Throwable error) {
			this.error.add(error.toString());
		}
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=ConsoleReporterTest test
```

Expected: FAIL because `ConsoleReporter` does not exist.

- [ ] **Step 3: Implement `ConsoleReporter`**

Create `ConsoleReporter.java`:

```java
package dev.feit.slop4j.maven.plugin.internal;

import dev.feit.slop4j.SlopFinding;
import java.util.Objects;
import org.apache.maven.plugin.logging.Log;

public final class ConsoleReporter {

	private final Log log;

	public ConsoleReporter(Log log) {
		this.log = Objects.requireNonNull(log, "log");
	}

	public void reportSkipped() {
		log.info("Slop4J Audit skipped.");
	}

	public void reportNoFiles(boolean failIfNoFiles) {
		log.info("Slop4J Audit");
		if (failIfNoFiles) {
			log.error("No files matched the configured slop audit includes.");
		} else {
			log.warn("No files matched the configured slop audit includes.");
		}
	}

	public void reportSummary(AuditSummary summary, AuditConfiguration configuration) {
		log.info("Slop4J Audit");
		log.info("Scanned " + summary.scannedFileCount() + " file(s).");
		for (AuditDecision decision : summary.decisions()) {
			reportDecision(decision, configuration);
		}
		if (summary.maximumSlopViolationCount() > 0) {
			log.error("Build failed because " + summary.maximumSlopViolationCount()
					+ " file(s) exceeded maxSlopScore=" + configuration.maxSlopScore() + ".");
		}
		if (summary.minimumSlopViolationCount() > 0) {
			log.error("Build failed because " + summary.minimumSlopViolationCount()
					+ " file(s) were below minSlopScore=" + configuration.minSlopScore() + ".");
		}
		if (!summary.hasPolicyViolations()) {
			log.info("Slop4J Audit completed without policy violations.");
		}
	}

	private void reportDecision(AuditDecision decision, AuditConfiguration configuration) {
		FileSlopResult result = decision.result();
		String line = result.displayPath() + " slopScore=" + result.report().slopScore() + " verdict="
				+ result.report().verdict() + " findings=" + result.report().findings().size();
		if (decision.exceedsMaximumSlop()) {
			log.warn(line);
			result.report().findings().stream()
					.limit(configuration.maxFindingsPerFile())
					.forEach(this::warnFinding);
		} else if (decision.belowMinimumSlop()) {
			log.error(result.displayPath() + " is dangerously specific.");
			log.error("  slopScore=" + result.report().slopScore() + " is below minSlopScore="
					+ configuration.minSlopScore() + ".");
			log.error("  verdict=" + result.report().verdict());
			log.error("  This may reduce strategic optionality.");
		} else {
			log.info(line);
		}
	}

	private void warnFinding(SlopFinding finding) {
		String evidence = finding.evidence().isBlank() ? "" : " Evidence: " + finding.evidence();
		log.warn("  " + finding.type() + " " + finding.severity() + ": " + finding.message() + evidence);
	}
}
```

- [ ] **Step 4: Run reporter tests**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=ConsoleReporterTest test
```

Expected: PASS.

- [ ] **Step 5: Commit reporter**

```bash
git add slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/internal/ConsoleReporter.java slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/internal/ConsoleReporterTest.java
git commit -m "feat: report slop audit results"
```

### Task 8: Implement `slop4j:audit` Mojo

**Files:**
- Create: `slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/SlopAuditMojo.java`
- Create: `slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/SlopAuditMojoTest.java`

- [ ] **Step 1: Write failing Mojo tests**

Create `SlopAuditMojoTest.java`:

```java
package dev.feit.slop4j.maven.plugin;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlopAuditMojoTest {

	@TempDir
	Path tempDir;

	@Test
	void skipsExecutionWhenConfigured() throws Exception {
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "skip", true);

		assertThatCode(mojo::execute).doesNotThrowAnyException();
	}

	@Test
	void succeedsWhenNoFilesMatchAndFailureDisabled() throws Exception {
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("docs/**/*.md"));
		set(mojo, "failIfNoFiles", false);

		assertThatCode(mojo::execute).doesNotThrowAnyException();
	}

	@Test
	void failsWhenNoFilesMatchAndFailureEnabled() throws Exception {
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("docs/**/*.md"));
		set(mojo, "failIfNoFiles", true);

		assertThatThrownBy(mojo::execute)
				.isInstanceOf(MojoExecutionException.class)
				.hasMessage("No files matched the configured slop audit includes.");
	}

	@Test
	void failsWhenMaxSlopScoreIsExceeded() throws Exception {
		Files.writeString(tempDir.resolve("README.md"),
				"We leverage agentic AI to unlock seamless enterprise-grade transformation across modern workflows.");
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("README.md"));
		set(mojo, "maxSlopScore", 10.0);
		set(mojo, "failOnSlop", true);

		assertThatThrownBy(mojo::execute)
				.isInstanceOf(MojoExecutionException.class)
				.hasMessageContaining("exceeded maxSlopScore=10.0");
	}

	@Test
	void failsWhenTooConcreteModeIsViolated() throws Exception {
		Files.createDirectories(tempDir.resolve("docs"));
		Files.writeString(tempDir.resolve("docs/architecture.md"),
				"Create PaymentController.java. Run mvn test. Store PostgreSQL 16 migrations in db/migration.");
		SlopAuditMojo mojo = new SlopAuditMojo();
		set(mojo, "basedir", tempDir.toFile());
		set(mojo, "includes", List.of("docs/**/*.md"));
		set(mojo, "failOnSlop", false);
		set(mojo, "failIfTooConcrete", true);
		set(mojo, "minSlopScore", 99.0);

		assertThatThrownBy(mojo::execute)
				.isInstanceOf(MojoExecutionException.class)
				.hasMessageContaining("below minSlopScore=99.0");
	}

	private static void set(Object target, String fieldName, Object value) throws Exception {
		Field field = SlopAuditMojo.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=SlopAuditMojoTest test
```

Expected: FAIL because `SlopAuditMojo` does not exist.

- [ ] **Step 3: Implement `SlopAuditMojo`**

Create `SlopAuditMojo.java`:

```java
package dev.feit.slop4j.maven.plugin;

import dev.feit.slop4j.maven.plugin.internal.AuditConfiguration;
import dev.feit.slop4j.maven.plugin.internal.AuditDecision;
import dev.feit.slop4j.maven.plugin.internal.AuditSummary;
import dev.feit.slop4j.maven.plugin.internal.ConsoleReporter;
import dev.feit.slop4j.maven.plugin.internal.FileSlopResult;
import dev.feit.slop4j.maven.plugin.internal.MarkdownFileScanner;
import dev.feit.slop4j.maven.plugin.internal.SlopFileAuditor;
import dev.feit.slop4j.maven.plugin.internal.ThresholdPolicy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "audit", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true, requiresProject = true)
public final class SlopAuditMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.basedir}", readonly = true, required = true)
	private File basedir;

	@Parameter(defaultValue = "60.0")
	private double maxSlopScore = 60.0;

	@Parameter(defaultValue = "80.0")
	private double minSlopScore = 80.0;

	@Parameter(defaultValue = "true")
	private boolean failOnSlop = true;

	@Parameter(defaultValue = "false")
	private boolean failIfTooConcrete;

	@Parameter
	private List<String> languages;

	@Parameter
	private List<String> includes;

	@Parameter
	private List<String> excludes;

	@Parameter(property = "slop4j.skip", defaultValue = "false")
	private boolean skip;

	@Parameter(defaultValue = "false")
	private boolean failIfNoFiles;

	@Parameter(defaultValue = "5")
	private int maxFindingsPerFile = 5;

	@Parameter(defaultValue = "120")
	private int maxFindingEvidenceLength = 120;

	@Override
	public void execute() throws MojoExecutionException {
		ConsoleReporter reporter = new ConsoleReporter(getLog());
		if (skip) {
			reporter.reportSkipped();
			return;
		}
		AuditConfiguration configuration = createConfiguration();
		try {
			List<Path> files = new MarkdownFileScanner(configuration.baseDirectory())
					.scan(configuration.includes(), configuration.excludes());
			if (files.isEmpty()) {
				reporter.reportNoFiles(configuration.failIfNoFiles());
				if (configuration.failIfNoFiles()) {
					throw new MojoExecutionException("No files matched the configured slop audit includes.");
				}
				return;
			}
			List<FileSlopResult> results = new SlopFileAuditor(configuration).audit(files);
			ThresholdPolicy policy = new ThresholdPolicy(configuration);
			List<AuditDecision> decisions = results.stream().map(policy::evaluate).toList();
			AuditSummary summary = new AuditSummary(results, decisions);
			reporter.reportSummary(summary, configuration);
			failOnPolicyViolations(summary, configuration);
		} catch (IOException exception) {
			throw new MojoExecutionException("Failed to run Slop4J audit.", exception);
		}
	}

	private AuditConfiguration createConfiguration() throws MojoExecutionException {
		try {
			return AuditConfiguration.create(basedir, maxSlopScore, minSlopScore, failOnSlop, failIfTooConcrete,
					languages, includes, excludes, failIfNoFiles, maxFindingsPerFile, maxFindingEvidenceLength);
		} catch (IllegalArgumentException exception) {
			throw new MojoExecutionException(exception.getMessage(), exception);
		}
	}

	private static void failOnPolicyViolations(AuditSummary summary, AuditConfiguration configuration)
			throws MojoExecutionException {
		if (summary.maximumSlopViolationCount() > 0) {
			throw new MojoExecutionException("Build failed because " + summary.maximumSlopViolationCount()
					+ " file(s) exceeded maxSlopScore=" + configuration.maxSlopScore() + ".");
		}
		if (summary.minimumSlopViolationCount() > 0) {
			throw new MojoExecutionException("Build failed because " + summary.minimumSlopViolationCount()
					+ " file(s) were below minSlopScore=" + configuration.minSlopScore() + ".");
		}
	}
}
```

- [ ] **Step 4: Run Mojo tests**

Run:

```bash
mvn -pl slop4j-maven-plugin -Dtest=SlopAuditMojoTest test
```

Expected: PASS.

- [ ] **Step 5: Run plugin module test suite**

Run:

```bash
mvn -pl slop4j-maven-plugin -am test
```

Expected: PASS.

- [ ] **Step 6: Commit Mojo**

```bash
git add slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/SlopAuditMojo.java slop4j-maven-plugin/src/test/java/dev/feit/slop4j/maven/plugin/SlopAuditMojoTest.java
git commit -m "feat: add slop audit maven goal"
```

### Task 9: Verify Plugin Descriptor and Local Invocation

**Files:**
- Modify only if verification exposes descriptor or packaging issues.

- [ ] **Step 1: Verify plugin packaging**

Run:

```bash
mvn -pl slop4j-maven-plugin -am verify
```

Expected: PASS and plugin descriptor generated under:

```text
slop4j-maven-plugin/target/classes/META-INF/maven/plugin.xml
```

- [ ] **Step 2: Inspect generated descriptor**

Run:

```bash
sed -n '1,220p' slop4j-maven-plugin/target/classes/META-INF/maven/plugin.xml
```

Expected: descriptor contains:

```xml
<goal>audit</goal>
<implementation>dev.feit.slop4j.maven.plugin.SlopAuditMojo</implementation>
<language>java</language>
```

It should also contain parameters named:

```text
maxSlopScore
minSlopScore
failOnSlop
failIfTooConcrete
languages
includes
excludes
skip
failIfNoFiles
maxFindingsPerFile
maxFindingEvidenceLength
```

- [ ] **Step 3: Install current reactor artifacts locally for invocation testing**

Run:

```bash
mvn -DskipTests install
```

Expected: PASS.

- [ ] **Step 4: Invoke the goal against this repository in non-failing mode**

Run:

```bash
mvn dev.feit:slop4j-maven-plugin:0.1.0-SNAPSHOT:audit -DfailOnSlop=false
```

Expected: PASS and console starts with:

```text
Slop4J Audit
```

It should scan at least `README.md`, `README_DE.md`, and files under `docs/`.

- [ ] **Step 5: Invoke skip mode**

Run:

```bash
mvn dev.feit:slop4j-maven-plugin:0.1.0-SNAPSHOT:audit -Dslop4j.skip=true
```

Expected: PASS and console contains:

```text
Slop4J Audit skipped.
```

- [ ] **Step 6: Commit descriptor fixes if any were needed**

If no files changed, skip this commit. If fixes were needed:

```bash
git add pom.xml slop4j-maven-plugin/pom.xml slop4j-maven-plugin/src/main/java/dev/feit/slop4j/maven/plugin/SlopAuditMojo.java
git commit -m "fix: generate slop audit plugin descriptor"
```

### Task 10: Document Maven Plugin Usage

**Files:**
- Modify: `README.md`
- Modify: `README_DE.md`

- [ ] **Step 1: Add English README section**

Add this section after the AssertJ section or after Quick Start if no AssertJ
section exists:

```markdown
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
```

- [ ] **Step 2: Add synchronized German README section**

Add the German counterpart to `README_DE.md`:

```markdown
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
```

- [ ] **Step 3: Verify README synchronization**

Run:

```bash
rg -n "slop4j-maven-plugin|failIfTooConcrete|minSlopScore" README.md README_DE.md
```

Expected: both README files contain the plugin artifact name, `failIfTooConcrete`, and `minSlopScore`.

- [ ] **Step 4: Commit documentation**

```bash
git add README.md README_DE.md
git commit -m "docs: document slop audit maven plugin"
```

### Task 11: Run Formatting and Full Verification

**Files:**
- Potentially modified by formatter: Java files under `slop4j-maven-plugin/src/main/java` and `slop4j-maven-plugin/src/test/java`

- [ ] **Step 1: Apply formatting**

Run:

```bash
mvn spotless:apply
```

Expected: PASS.

- [ ] **Step 2: Run targeted plugin verification**

Run:

```bash
mvn -pl slop4j-maven-plugin -am verify
```

Expected: PASS.

- [ ] **Step 3: Run full project verification**

Run:

```bash
mvn clean verify
```

Expected: PASS.

- [ ] **Step 4: Verify formatting**

Run:

```bash
mvn spotless:check
```

Expected: PASS.

- [ ] **Step 5: Inspect final git diff**

Run:

```bash
git diff --stat
git diff --check
```

Expected: `git diff --check` reports no whitespace errors.

- [ ] **Step 6: Commit verification cleanup if formatter changed files**

If `mvn spotless:apply` changed files after the previous commits:

```bash
git add slop4j-maven-plugin README.md README_DE.md pom.xml slop4j-bom/pom.xml
git commit -m "style: format slop audit maven plugin"
```

## 8. Acceptance Criteria

Step 3 is complete when:

- `slop4j-maven-plugin` exists as a Maven module.
- The root reactor includes `slop4j-maven-plugin`.
- `slop4j-bom` manages `slop4j-maven-plugin`.
- The plugin artifact uses `maven-plugin` packaging.
- The generated plugin goal prefix is `slop4j`.
- The generated goal is `audit`.
- `SlopAuditMojo` is annotated as thread-safe and bound to `verify` by default.
- The plugin scans `README.md`, `README_DE.md`, `docs/**/*.md`, and `adr/**/*.md` by default.
- The plugin supports `languages`, `includes`, `excludes`, `maxSlopScore`, `failOnSlop`, `failIfTooConcrete`, `minSlopScore`, `skip`, `failIfNoFiles`, `maxFindingsPerFile`, and `maxFindingEvidenceLength`.
- Language codes `en`, `english`, `de`, `german`, and `deutsch` are supported.
- File matching is deterministic and sorted by project-relative path.
- UTF-8 file reading is used.
- Markdown is analyzed as-is without stripping Markdown syntax.
- The plugin reports scanned file count, per-file score, verdict, and finding count.
- `failOnSlop=true` fails when at least one file exceeds `maxSlopScore`.
- `failIfTooConcrete=true` fails when at least one file is below `minSlopScore`.
- `slop4j.skip=true` skips execution.
- `failIfNoFiles=true` fails when no files match.
- README and README_DE document the plugin with synchronized content.
- `mvn -pl slop4j-maven-plugin -am verify` passes.
- `mvn clean verify` passes.
- `mvn spotless:check` passes.

## 9. Non-Goals

These features remain explicitly out of Step 3:

- JSON report file generation.
- HTML report file generation.
- CLI packaging.
- GitLab CI templates.
- Automatic language detection.
- Markdown AST parsing.
- External NLP libraries.
- External AI or API calls.
- New core analyzer rules.
- Configuration files outside Maven plugin parameters.

## 10. Implementation Notes

- Keep all public user-facing plugin documentation serious and dry.
- Keep `SlopAuditMojo` thin. If the Mojo grows past orchestration and Maven parameter binding, move logic into an internal class.
- Do not place reusable plugin logic in `slop4j-core`; the core must remain independent from Maven.
- Do not depend on `slop4j-assertj` from the plugin. The AssertJ module is for tests and consumers, not plugin runtime.
- Prefer immutable records for internal value objects.
- Use `List.copyOf(...)` in record compact constructors.
- Use `Path` for internal filesystem operations and normalize display output only at reporting boundaries.
- Preserve the existing README synchronization rule: every content change in `README.md` must be reflected in `README_DE.md`.
