package dev.feit.slop4j.internal;

public final class ScoreMath {

    private ScoreMath() {}

    public static double saturating(double value, double scale) {
        if (value <= 0.0) {
            return 0.0;
        }
        return clamp01(1.0 - Math.exp(-value / scale));
    }

    public static double clamp01(double value) {
        if (Double.isNaN(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    public static double ratio(double numerator, double denominator) {
        if (denominator <= 0.0) {
            return 0.0;
        }
        return numerator / denominator;
    }

    public static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
