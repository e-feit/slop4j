# slop4j - Agent Instructions

This guide helps future agent sessions understand the non-obvious architecture constraints and guidelines for this repository.

## 1. Core Mandates
* **Tone (Crucial Satire):** `slop4j` is a satirical "AI Output Governance Framework" using rule-based heuristics. Keep all code, JavaDocs, and public docs ultra-serious and enterprise-grade. No jokes in code.
* **README Sync:** English (`README.md`) and German (`README_DE.md`) must remain strictly synchronized.
* **Dependency Minimalism:** `slop4j-core` must remain minimal (SnakeYAML only). No Spring/NLP libraries in core.
* **Java Version:** Target Java 17 (Records, Sealed classes, Pattern matching).
* **Nullability:** Use JSpecify (`@NullMarked`, `@Nullable`) with `provided` scope.

## 2. Technical Map & Docs
Agents MUST read these before making structural changes:
- [Architecture Context](docs/architecture/context.md): Technical engine overview.
- [Module Map](docs/architecture/module-map.md): Dependencies and boundaries.
- [Runtime & Deployment](docs/architecture/runtime.md): JVM distribution model.
- [ADRs](docs/adr/README.md): Architecture Decision Records.

## 3. Maintenance Rules
- **Keep it small:** Documentation must remain highly compressed (< 500 lines).
- **Update on Change:** Structural changes (new modules, scoring logic shifts) MUST be reflected in `docs/architecture/`.
- **ADR Policy:** Significant decisions require a new ADR in `docs/adr/YYYY-MM-DD-description.md`.
- **No Hallucinations:** Explicitly mark missing information as "TBD" or "Unknown".

## 4. Verification Commands
- **Build & Test:** `mvn clean test`
- **Format Code:** `mvn spotless:apply`
- **Check Formatting:** `mvn spotless:check`
- **Requirement:** Pushes are only allowed if both tests and spotless check pass.
