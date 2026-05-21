# Maven Plugin Usage Example

This example configures `slop4j-maven-plugin` as part of the normal Maven build.
The plugin scans `docs/audit-sample.md` and keeps the configured threshold above
the sample score so the example remains buildable.

Run it as part of the full reactor:

```bash
mvn clean verify
```
