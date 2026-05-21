# CLI Usage Example

This example contains a Markdown document that can be audited with
`slop4j-cli` after the CLI module has been packaged.

Run from the repository root:

```bash
mvn -pl slop4j-cli -am package
java -jar slop4j-cli/target/slop4j-cli-0.1.0-SNAPSHOT.jar audit slop4j-examples/cli-usage/docs/audit-sample.md --lang en --max-score 85
```
