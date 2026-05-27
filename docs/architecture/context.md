# Architecture Context

## Technical Overview
`slop4j` is a deterministic scoring engine for text "slop" (generic AI-generated output). It replaces stochastic AI models with fast, rule-based heuristics.

## Core Engine (slop4j-core)
- **Tokenization:** Regex-based (`[\\p{L}\\p{N}][\\p{L}\\p{N}\\-']*`).
- **Lexicon:** YAML-based dictionaries under `dev/feit/slop4j/lexicon/`.
- **Scoring Logic:**
    - **Heuristic Rules:** Actionability, Buzzwords, Overconfidence, Repetition, Vague Phrases.
    - **Length Gating:** Penalties are scaled for texts < 80 tokens to avoid false positives on short inputs.
    - **Scoring Factor:** `context.tokenCount() / 80.0` (clamped 0-1).

## Data Flow
1. **Input:** Raw text + Language selection.
2. **Analysis:** `SlopAnalyzer` tokenizes text, splits into sentences, and runs a list of `SlopRule`s.
3. **Synthesis:** `DefaultSlopScorer` aggregates findings into a `SlopVerdict` (Score 0.0-1.0).
4. **Output:** `SlopReport` containing findings and the final score.
