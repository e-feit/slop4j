# Architecture Decision Records (ADR)

We use ADRs to track significant architectural decisions.

## Format
Files MUST follow the naming convention: `YYYY-MM-DD-description.md`.

## Rules
- **Immutable History:** Never silently overwrite or delete an ADR.
- **Superseding:** If a decision is changed, mark the old ADR as `Status: Superseded by [link]` and create a new one.
- **Lightweight:** Focus on the "Why" (Context, Decision, Consequences) instead of implementation details.
