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
2. **Release:** A numeric tag on the current default-branch head sets `${revision}` for Maven, signs artifacts, and automatically publishes through the Sonatype Central Publishing Plugin. The job waits for the published state and advances the default branch to the next patch snapshot.
3. **Artifacts:** Includes `-sources.jar` and `-javadoc.jar` for all public modules.
