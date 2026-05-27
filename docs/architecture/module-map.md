# Module Map

## Dependency Graph
- `slop4j-core`: Zero external runtime dependencies (except SnakeYAML).
- `slop4j-bom`: Bill of Materials.
- `slop4j-assertj` -> `slop4j-core`: Custom testing assertions.
- `slop4j-cli` -> `slop4j-core`: Fat JAR distribution.
- `slop4j-maven-plugin` -> `slop4j-core`: Build-time analysis.
- `slop4j-spring-boot-starter` -> `slop4j-core`: Auto-configuration.

## Boundaries
- **Core Isolation:** `slop4j-core` must NEVER depend on any other project module or external frameworks (Spring, etc.).
- **Internal API:** Classes in `dev.feit.slop4j.internal` are strictly private to the library. No integration should depend on internal classes.
- **Examples:** `slop4j-examples` must track all public API changes but are excluded from Maven Central publishing.
