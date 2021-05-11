package com.murengezi.minecraft.client.resources;

public class Language implements Comparable<Language> {

	private final String languageCode, region, name;
	private final boolean bidirectional;

	public Language(String languageCode, String region, String name, boolean bidirectional) {
		this.languageCode = languageCode;
		this.region = region;
		this.name = name;
		this.bidirectional = bidirectional;
	}

	public String getLanguageCode() {
		return this.languageCode;
	}

	public boolean isBidirectional() {
		return this.bidirectional;
	}

	public String toString() {
		return String.format("%s (%s)", this.name, this.region);
	}

	public boolean equals(Object obj) {
		return this == obj || (obj instanceof Language && this.languageCode.equals(((Language) obj).languageCode));
	}

	public int hashCode() {
		return this.languageCode.hashCode();
	}

	public int compareTo(Language language) {
		return this.languageCode.compareTo(language.languageCode);
	}

}
