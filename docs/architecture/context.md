# Architecture Context

## Technical Overview
`slop4j` is a deterministic scoring engine for text "slop" (generic AI-generated output). It replaces stochastic AI models with fast, rule-based heuristics.

## Core Engine (slop4j-core)
- **Tokenization:** Regex-based (`[\\p{L}\\p{N}][\\p{L}\\p{N}\\-']*`); sentence splitting supports local claim analysis.
- **Lexicon:** YAML-based dictionaries under `dev/feit/slop4j/lexicon/`.
- **Scoring Logic:**
    - **Heuristic Rules:** Actionability, Buzzwords, Overconfidence, Repetition, Vague Phrases, Concrete Anchors, Evidence.
    - **Phrase Matches:** Vague phrases use Unicode word boundaries and count each occurrence.
    - **Claim Support:** Absolute claim markers are penalized only when their sentence lacks a citation or measured result.
    - **Distinct Signals:** Technical anchors determine concreteness; cited sources and measured results determine evidence.
    - **Length Gating:** Missing evidence, actionability, and concreteness penalties are scaled for texts < 80 tokens.
    - **Scoring Factor:** `context.tokenCount() / 80.0` (clamped 0-1).

## Data Flow
1. **Input:** Raw text + Language selection.
2. **Analysis:** `SlopAnalyzer` tokenizes text, splits into sentences, and runs a list of `SlopRule`s.
3. **Synthesis:** `DefaultSlopScorer` aggregates findings into a `SlopVerdict` (Slop Score 0.0-100.0).
4. **Output:** `SlopReport` containing findings and the final score.
