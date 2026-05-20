package dev.feit.slop4j;

public enum SlopVerdict {
    /**
     * The analyzed text shows no material slop indicators and remains below the lowest standard
     * score threshold.
     */
    CLEAN,

    /**
     * The analyzed text contains a limited amount of generic or weakly actionable language while
     * remaining within an acceptable score range.
     */
    ACCEPTABLY_FLUFFY,

    /**
     * The analyzed text contains enough slop indicators to require attention, but not enough to
     * qualify as a high-severity narrative risk.
     */
    SLOP_ADJACENT,

    /**
     * The analyzed text contains a high concentration of vague, generic, or overly polished
     * language patterns.
     */
    LINKEDIN_READY,

    /**
     * The analyzed text reaches the highest standard slop score range and is dominated by generic,
     * low-specificity, or insufficiently actionable language.
     */
    BOARD_APPROVED_SLOP,

    /**
     * The analyzed text has a low slop score while also demonstrating strong concreteness and
     * actionability signals.
     */
    DANGEROUSLY_USEFUL,
}
