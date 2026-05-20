package dev.feit.slop4j.internal.rules;

import dev.feit.slop4j.Severity;
import dev.feit.slop4j.SlopFinding;
import dev.feit.slop4j.SlopFindingType;
import dev.feit.slop4j.internal.SlopContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConcreteAnchorRule implements SlopRule {

    static final Pattern NUMBER = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
    static final Pattern VERSION = Pattern.compile("\\bv?\\d+\\.\\d+(\\.\\d+)?\\b");
    static final Pattern CODE_BLOCK = Pattern.compile("(?s)```.*?```");
    static final Pattern FILE_NAME =
            Pattern.compile("\\b[\\w.-]+\\.(java|ts|js|json|yaml|yml|xml|md|sql|sh|properties)\\b");
    static final Pattern METHOD_NAME = Pattern.compile("\\b[a-z][a-zA-Z0-9]*\\s*\\(");
    static final Pattern CLASS_NAME =
            Pattern.compile(
                    "\\b[A-Z][A-Za-z0-9]+(?:Service|Controller|Repository|Factory|Builder|Config|Configuration|Exception|Client|Mapper)\\b");
    static final Pattern CLI_COMMAND =
            Pattern.compile(
                    "(?m)^\\s*(mvn|npm|npx|pnpm|yarn|git|docker|kubectl|helm|java|curl)\\b.*$");

    @Override
    public Result analyze(SlopContext context, int maxFindingEvidenceLength) {
        List<String> anchors = new ArrayList<>();
        double weightedHits = 0.0;

        weightedHits += collect(context.originalText(), CODE_BLOCK, 4.0, anchors);
        weightedHits += collect(context.originalText(), CLI_COMMAND, 3.0, anchors);
        weightedHits += collect(context.originalText(), FILE_NAME, 2.0, anchors);
        weightedHits += collect(context.originalText(), VERSION, 2.0, anchors);
        weightedHits += collect(context.originalText(), NUMBER, 1.0, anchors);
        weightedHits += collect(context.originalText(), METHOD_NAME, 1.0, anchors);
        weightedHits += collect(context.originalText(), CLASS_NAME, 1.0, anchors);

        for (String token : context.tokens()) {
            if (context.dictionaries().concreteTerms().contains(token)) {
                weightedHits += 1.0;
                anchors.add(token);
            }
        }

        double evidenceScore = Math.min(1.0, weightedHits / 10.0);
        double concretenessScore = evidenceScore;
        List<SlopFinding> findings = new ArrayList<>();
        if (context.tokenCount() >= 80 && evidenceScore < 0.2) {
            findings.add(
                    new SlopFinding(
                            SlopFindingType.LOW_EVIDENCE,
                            Severity.WARNING,
                            "Text contains few concrete anchors such as numbers, versions, commands or file names.",
                            ""));
            findings.add(
                    new SlopFinding(
                            SlopFindingType.LOW_CONCRETENESS,
                            Severity.WARNING,
                            "Text provides limited concrete implementation detail.",
                            ""));
        } else if (context.tokenCount() >= 20 && evidenceScore < 0.2) {
            findings.add(
                    new SlopFinding(
                            SlopFindingType.LOW_CONCRETENESS,
                            Severity.INFO,
                            "Text provides limited concrete implementation detail.",
                            ""));
        }

        return new Result(0.0, 0.0, 0.0, concretenessScore, 0.0, evidenceScore, 0.0, 0.0, findings);
    }

    private static double collect(
            String text, Pattern pattern, double weight, List<String> anchors) {
        Matcher matcher = pattern.matcher(text);
        double score = 0.0;
        while (matcher.find()) {
            anchors.add(matcher.group().strip());
            score += weight;
        }
        return score;
    }
}
