package com.murengezi.minecraft.client.Gui.Options.ResourcePack;

import net.minecraft.client.resources.I18n;

import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 15:58
 */
public class ResourcePackSelectedList extends ResourcePackList {

	public ResourcePackSelectedList(int width, int height, List<ResourcePackListEntry> resourcePackListEntries) {
		super(width, height, resourcePackListEntries);
	}

	@Override
	protected String getListHeader() {
		return I18n.format("resourcePack.selected.title");
	}

}
