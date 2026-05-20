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
     * The analyzed text is a void of meaning, perfectly optimized for corporate environments
     * where saying nothing is the safest strategy.
     */
    TOTAL_CORPORATE_NOTHINGNESS,

    /**
     * The analyzed text contains a high concentration of vague, generic, or overly polished
     * language patterns.
     */
    LINKEDIN_READY,

    /**
     * The analyzed text is high-quality waste. It is grammatically perfect and aesthetically
     * pleasing while remaining entirely devoid of value.
     */
    PREMIUM_POLISHED_GARBAGE,

    /**
     * The analyzed text reaches the highest standard slop score range and is dominated by generic,
     * low-specificity, or insufficiently actionable language.
     */
    BOARD_APPROVED_SLOP,

    /**
     * The analyzed text is the result of a direct pipeline from a low-quality prompt to an
     * unfiltered output, bypassing all human editorial judgment.
     */
    GARBAGE_IN_SLOP_OUT,

    /**
     * The ultimate peak of slop. The text shows zero signs of cognitive involvement and has
     * reached a state of pure, distilled content-free existence.
     */
    CERTIFIED_BRAINLESS_SLOP,

    /**
     * The analyzed text has a low slop score while also demonstrating strong concreteness and
     * actionability signals.
     */
    DANGEROUSLY_USEFUL,

    /**
     * The analyzed text displays extreme confidence while providing near-zero evidence,
     * a hallmark of advanced stochastic hallucination.
     */
    BRAIN_FREE_ZONE,
}
