package dev.feit.slop4j;

public enum Language {
	ENGLISH("en"), GERMAN("de");

	private final String code;

	Language(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}
}
