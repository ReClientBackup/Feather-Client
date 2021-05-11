package com.murengezi.minecraft.client.resources.data;

import com.murengezi.minecraft.client.resources.Language;

import java.util.Collection;

public class LanguageMetadataSection implements IMetadataSection {

	private final Collection<Language> languages;

	public LanguageMetadataSection(Collection<Language> languages) {
		this.languages = languages;
	}

	public Collection<Language> getLanguages() {
		return this.languages;
	}

}
