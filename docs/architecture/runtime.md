# Runtime & Deployment

## Runtime Environment
- **Platform:** Java Virtual Machine (JVM).
- **Target:** Java 17+.
- **Statelessness:** The library is strictly stateless and thread-safe.

## Distribution
- **Format:** Standard Maven Artifacts (JAR).
- **Registry:** Maven Central.
- **Versioning:** Semantic Versioning via `${revision}` property in root POM.

## Deployment Process
1. **CI Pipeline:** Runs `mvn clean verify`.
2. **Release:** Automated via GPG signing and Sonatype Central Publishing Plugin.
3. **Artifacts:** Includes `-sources.jar` and `-javadoc.jar` for all public modules.
