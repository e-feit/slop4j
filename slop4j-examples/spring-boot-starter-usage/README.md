# Spring Boot Starter Usage Example

This example shows Spring Boot auto-configuration for `SlopAnalyzer`.

Run from the repository root:

```bash
mvn -pl slop4j-examples/spring-boot-starter-usage -am test
```

The example application injects `SlopAnalyzer` directly into a controller. The
bean is provided by `slop4j-spring-boot-starter`; no application properties are
required for the default English analyzer.

Optional analyzer configuration:

```yaml
slop4j:
  languages:
    - en
    - de
  max-finding-evidence-length: 120
```
