package com.murengezi.minecraft.client.Gui.Options.ResourcePack;

import com.murengezi.minecraft.client.Gui.GuiListExtended;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 15:58
 */
public abstract class ResourcePackList extends GuiListExtended {

	protected final List<ResourcePackListEntry> resourcePackListEntries;
	public ResourcePackList(int width, int height, List<ResourcePackListEntry> resourcePackListEntries) {
		super(width, height, 32, height - 55 + 4, 36);
		this.resourcePackListEntries = resourcePackListEntries;
		this.field_148163_i = false;
		this.setHasListHeader(true, (int)((float)getFr().FONT_HEIGHT * 1.5F));
	}

	protected void drawListHeader(int x, int y) {
		String s = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.getListHeader();
		getFr().drawString(s, x + this.width / 2 - getFr().getStringWidth(s) / 2, Math.min(this.top + 3, y), 16777215);
	}

	protected abstract String getListHeader();

	public List<ResourcePackListEntry> getList() {
		return this.resourcePackListEntries;
	}

	protected int getSize() {
		return this.getList().size();
	}

	public ResourcePackListEntry getListEntry(int index) {
		return this.getList().get(index);
	}

	public int getListWidth() {
		return this.width;
	}

	protected int getScrollBarX() {
		return this.right - 6;
	}

}
