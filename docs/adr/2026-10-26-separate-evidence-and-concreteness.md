# Separate Evidence and Concreteness Signals

Status: Accepted

## Context

The initial scorer used technical anchors for both concreteness and evidence. A file name or version number could therefore appear to substantiate an unrelated absolute claim. Vague phrases were matched once per dictionary entry, regardless of repeated occurrences.

## Decision

Keep deterministic rules in `slop4j-core`. Count bounded occurrences of vague phrases. Evaluate absolute claim markers within each sentence and treat citations or measured results in that sentence as support signals. Derive concreteness from technical anchors and evidence from the share of sentences containing support signals. The scorer no longer reduces the overconfidence penalty using document-wide evidence.

## Consequences

Existing documents can receive different scores and verdicts. Technical detail without citations or measurements can produce high concreteness and low evidence. Source and measurement detection remains heuristic; a signal in the same sentence does not prove the claim. The public `SlopReport` shape and core runtime dependencies remain unchanged.
