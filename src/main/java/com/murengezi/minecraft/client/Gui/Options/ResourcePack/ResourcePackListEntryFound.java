package com.murengezi.minecraft.client.Gui.Options.ResourcePack;

import net.minecraft.client.resources.ResourcePackRepository;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 15:50
 */
public class ResourcePackListEntryFound extends ResourcePackListEntry {

	private final ResourcePackRepository.Entry entry;

	public ResourcePackListEntryFound(ResourcePacksScreen resourcePacksScreen, ResourcePackRepository.Entry entry) {
		super(resourcePacksScreen);
		this.entry = entry;
	}

	@Override
	protected int getPackFormat() {
		return getEntry().getPackFormat();
	}

	@Override
	protected String getDescription() {
		return getEntry().getTexturePackDescription();
	}

	@Override
	protected String getName() {
		return getEntry().getResourcePackName();
	}

	@Override
	protected void loadIcon() {
		getEntry().bindTexturePackIcon(getMc().getTextureManager());
	}

	public ResourcePackRepository.Entry getEntry() {
		return this.entry;
	}
}
