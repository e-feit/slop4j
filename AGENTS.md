# slop4j - Agent Instructions

This guide helps future agent sessions understand the non-obvious architecture constraints and guidelines for this repository.

## 1. Tone and Humor (Crucial)
* **The Joke is the Discrepancty:** `slop4j` is a satirical "AI Output Governance Framework" that uses simple, rule-based heuristics instead of AI.
* **Keep Code/Docs Overly Serious:** All public APIs, JavaDocs, and the `README.md` must sound ultra-professional, enterprise-grade, and dry.
* **No Jokes in Code:** Never write jokes, easter eggs, or sarcastic remarks in code comments, commit messages, or PR descriptions. The humor must only emerge from the serious implementation of ridiculous concepts (e.g., `BOARD_APPROVED_SLOP`, `DANGEROUSLY_USEFUL`).

## 2. Technical Constraints (Do Not Violate)
* **Runtime Dependencies:** The `slop4j-core` module must keep runtime dependencies minimal. `org.yaml:snakeyaml` is permitted for YAML dictionary loading. No NLP libraries, no JSON parsers, no Spring. Testing may use JUnit 5 and AssertJ.
* **JSpecify for Nullability:** Use JSpecify annotations (such as `@NullMarked` and `@Nullable`) to define clear nullability contracts. These must be added with `provided` scope in Maven, ensuring zero runtime footprint.
* **Code Formatting (Spotless):** All Java files must be formatted using Spotless. Do not manually format or commit unformatted code.
* **Java Version:** Target Java 17. Use modern features like records, sealed classes, pattern matching, and text blocks where appropriate.
* **API Visibility:** Only classes directly in the `dev.feit.slop4j` package are public (`SlopAnalyzer`, `SlopReport`, etc.). Everything under `dev.feit.slop4j.internal` must be package-private or internal.
* **Automatic-Module-Name:** `dev.feit.slop4j` must be configured in the Maven build.

## 3. Algorithm & Processing Details (Easy to Miss)
* **Record Fields:** Do **not** use `Optional` as a field type in records (e.g., in `SlopFinding`). Use empty strings (`""`) to represent missing evidence instead.
* **Resource Loading:** Dictionaries are loaded from `.yaml` files under `dev/feit/slop4j/languages/` using SnakeYAML.
  * **Normalization:** Dictionary values must be stripped, lowercased using `Locale.ROOT`, and duplicates/empty entries removed upon loading.
  * **Merge Behavior:** When multiple languages are active, dictionaries are merged. There is no automatic language detection in Step 1.
* **Tokenizer Regex:** Always use exactly: `Pattern.compile("[\\p{L}\\p{N}][\\p{L}\\p{N}\\-']*")` to correctly tokenise words with hyphens and umlauts.
* **Sentence Splitting:** Use `text.split("(?<=[.!?])\\s+")`.
* **Length Gating:** Short texts (under 80 tokens) must not be fully penalized for low evidence, low actionability, or low concreteness. Apply a scaling factor: `lengthFactor = Math.min(1.0, Math.max(0.0, tokenCount / 80.0))` to these penalties.

## 4. Verification Commands
Once the Maven structure is initialized, use these commands:
* **Build and Test:** `mvn clean test`
* **Format Code:** `mvn spotless:apply`
* **Verify Formatting:** `mvn spotless:check`
