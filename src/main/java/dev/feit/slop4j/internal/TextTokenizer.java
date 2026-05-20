package dev.feit.slop4j.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextTokenizer {

    private static final Pattern TOKEN = Pattern.compile("[\\p{L}\\p{N}][\\p{L}\\p{N}\\-']*");

    private TextTokenizer() {}

    public static List<String> tokens(String text) {
        Matcher matcher = TOKEN.matcher(text.toLowerCase(Locale.ROOT));
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return List.copyOf(tokens);
    }

    public static List<String> sentences(String text) {
        String stripped = text.strip();
        if (stripped.isEmpty()) {
            return List.of();
        }
        String[] parts = stripped.split("(?<=[.!?])\\s+");
        List<String> sentences = new ArrayList<>();
        for (String part : parts) {
            String sentence = part.strip();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
        }
        return List.copyOf(sentences);
    }
}
