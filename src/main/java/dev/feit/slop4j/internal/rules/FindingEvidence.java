package dev.feit.slop4j.internal.rules;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

final class FindingEvidence {

    private FindingEvidence() {}

    static String joinDistinct(Collection<String> values, int maxLength) {
        List<String> distinct = new LinkedHashSet<>(values).stream().toList();
        String evidence = String.join(", ", distinct);
        if (maxLength == 0 || evidence.length() <= maxLength) {
            return evidence;
        }
        if (maxLength <= 3) {
            return evidence.substring(0, maxLength);
        }
        return evidence.substring(0, maxLength - 3).stripTrailing() + "...";
    }
}
