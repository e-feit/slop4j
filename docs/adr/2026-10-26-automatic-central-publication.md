# Publish Maven Releases Automatically

Status: Accepted

## Context

The numeric-tag release pipeline uploaded a signed Maven bundle to Sonatype Central and waited for validation. Its `autoPublish=false` setting left publication as a separate manual action, so a successful pipeline did not establish that artifacts were available from Maven Central.

## Decision

For the `central-publish` profile, enable automatic publication and wait until Sonatype reports `published`. Keep the numeric-tag and current default-branch head checks, release-version override, artifact signing, and next-snapshot commit in the existing pipeline.

## Consequences

A successful release pipeline now confirms publication rather than validation alone. A publish failure prevents the next-snapshot commit and requires investigation before retrying. Published Maven coordinates remain immutable.
