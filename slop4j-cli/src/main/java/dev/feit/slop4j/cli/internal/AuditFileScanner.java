package dev.feit.slop4j.cli.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class AuditFileScanner {

	private final Path baseDirectory;

	public AuditFileScanner(Path baseDirectory) {
		this.baseDirectory = Objects.requireNonNull(baseDirectory, "baseDirectory").toAbsolutePath().normalize();
	}

	public List<Path> scan(List<String> includes, List<String> excludes) throws IOException {
		List<Pattern> includePatterns = compile(includes);
		List<Pattern> excludePatterns = compile(excludes);
		try (var stream = Files.walk(baseDirectory)) {
			return stream.filter(Files::isRegularFile)
					.filter(path -> isIncluded(path, includePatterns, excludePatterns))
					.sorted(Comparator.comparing(this::displayPath)).toList();
		}
	}

	String displayPath(Path path) {
		return baseDirectory.relativize(path.toAbsolutePath().normalize()).toString().replace('\\', '/');
	}

	private boolean isIncluded(Path path, List<Pattern> includes, List<Pattern> excludes) {
		String relativePath = displayPath(path);
		boolean included = includes.stream().anyMatch(pattern -> pattern.matcher(relativePath).matches());
		boolean excluded = excludes.stream().anyMatch(pattern -> pattern.matcher(relativePath).matches());
		return included && !excluded;
	}

	private static List<Pattern> compile(List<String> patterns) {
		return patterns.stream().map(AuditFileScanner::toRegex).map(Pattern::compile).toList();
	}

	private static String toRegex(String glob) {
		StringBuilder regex = new StringBuilder("^");
		for (int i = 0; i < glob.length(); i++) {
			char current = glob.charAt(i);
			if (current == '*') {
				boolean doubleStar = i + 1 < glob.length() && glob.charAt(i + 1) == '*';
				if (doubleStar) {
					boolean followedBySlash = i + 2 < glob.length() && glob.charAt(i + 2) == '/';
					if (followedBySlash) {
						regex.append("(?:.*/)?");
						i += 2;
					} else {
						regex.append(".*");
						i++;
					}
				} else {
					regex.append("[^/]*");
				}
			} else if (current == '?') {
				regex.append("[^/]");
			} else {
				if ("\\.[]{}()+-^$|".indexOf(current) >= 0) {
					regex.append('\\');
				}
				regex.append(current);
			}
		}
		regex.append('$');
		return regex.toString();
	}
}
