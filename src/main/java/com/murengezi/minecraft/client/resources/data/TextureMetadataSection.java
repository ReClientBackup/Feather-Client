package com.murengezi.minecraft.client.resources.data;

import java.util.Collections;
import java.util.List;

public class TextureMetadataSection implements IMetadataSection {

	private final boolean textureBlur, textureClamp;
	private final List<Integer> listMipmaps;

	public TextureMetadataSection(boolean textureBlur, boolean textureClamp, List<Integer> listMipmaps) {
		this.textureBlur = textureBlur;
		this.textureClamp = textureClamp;
		this.listMipmaps = listMipmaps;
	}

	public boolean getTextureBlur() {
		return this.textureBlur;
	}

	public boolean getTextureClamp() {
		return this.textureClamp;
	}

	public List<Integer> getListMipmaps() {
		return Collections.unmodifiableList(this.listMipmaps);
	}

}
