package com.murengezi.minecraft.client.gui.Options.ResourcePack;

import com.murengezi.feather.Util.MinecraftUtils;
import com.murengezi.minecraft.client.gui.GuiListExtended;
import com.murengezi.minecraft.client.gui.YesNoScreen;
import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 15:30
 */
public abstract class ResourcePackListEntry extends MinecraftUtils implements GuiListExtended.IGuiListEntry {

	private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
	private static final IChatComponent incompatible = new ChatComponentTranslation("resourcePack.incompatible");
	private static final IChatComponent incompatibleOld = new ChatComponentTranslation("resourcePack.incompatible.old");
	private static final IChatComponent incompatibleNew = new ChatComponentTranslation("resourcePack.incompatible.new");
	protected final ResourcePacksScreen resourcePacksScreen;

	public ResourcePackListEntry(ResourcePacksScreen resourcePacksScreen) {
		this.resourcePacksScreen = resourcePacksScreen;
	}


	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
		int i = this.getPackFormat();

		if (i != 1) {
			GlStateManager.colorAllMax();
			GUI.drawRect(x - 1, y - 1, x + listWidth - 9, y + slotHeight + 1, -8978432);
		}

		this.loadIcon();
		GlStateManager.colorAllMax();
		GUI.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		String s = this.getName();
		String s1 = this.getDescription();

		if ((getMc().gameSettings.touchscreen || isSelected) && this.func_148310_d()) {
			getMc().getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
			GUI.drawRect(x, y, x + 32, y + 32, -1601138544);
			GlStateManager.colorAllMax();
			int j = mouseX - x;
			int k = mouseY - y;

			if (i < 1) {
				s = incompatible.getFormattedText();
				s1 = incompatibleNew.getFormattedText();
			} else if (i > 1) {
				s = incompatible.getFormattedText();
				s1 = incompatibleOld.getFormattedText();
			}

			if (this.func_148309_e())
			{
				if (j < 32)
				{
					GUI.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				}
				else
				{
					GUI.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}
			else
			{
				if (this.func_148308_f())
				{
					if (j < 16)
					{
						GUI.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					}
					else
					{
						GUI.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.func_148314_g())
				{
					if (j < 32 && j > 16 && k < 16)
					{
						GUI.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					}
					else
					{
						GUI.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.func_148307_h())
				{
					if (j < 32 && j > 16 && k > 16)
					{
						GUI.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					}
					else
					{
						GUI.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		int i1 = getMc().fontRendererObj.getStringWidth(s);

		if (i1 > 157)
		{
			s = getMc().fontRendererObj.trimStringToWidth(s, 157 - getMc().fontRendererObj.getStringWidth("...")) + "...";
		}

		getMc().fontRendererObj.drawStringWithShadow(s, (float)(x + 32 + 2), (float)(y + 1), 16777215);
		List<String> list = getMc().fontRendererObj.listFormattedStringToWidth(s1, 157);

		for (int l = 0; l < 2 && l < list.size(); ++l)
		{
			getMc().fontRendererObj.drawStringWithShadow(list.get(l), (float)(x + 32 + 2), (float)(y + 12 + 10 * l), 8421504);
		}
	}


	protected abstract int getPackFormat();

	protected abstract String getDescription();

	protected abstract String getName();

	protected abstract void loadIcon();

	protected boolean func_148310_d() {
		return true;
	}

	protected boolean func_148309_e() {
		return !this.resourcePacksScreen.hasResourcePackEntry(this);
	}

	protected boolean func_148308_f() {
		return this.resourcePacksScreen.hasResourcePackEntry(this);
	}

	protected boolean func_148314_g() {
		List<ResourcePackListEntry> list = this.resourcePacksScreen.getListContaining(this);
		int i = list.indexOf(this);
		return i > 0 && list.get(i - 1).func_148310_d();
	}

	protected boolean func_148307_h() {
		List<ResourcePackListEntry> list = this.resourcePacksScreen.getListContaining(this);
		int i = list.indexOf(this);
		return i >= 0 && i < list.size() - 1 && list.get(i + 1).func_148310_d();
	}

	public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
	{
		if (this.func_148310_d() && p_148278_5_ <= 32)
		{
			if (this.func_148309_e())
			{
				this.resourcePacksScreen.markChanged();
				int packFormat = this.getPackFormat();

				if (packFormat != 1) {
					String s1 = I18n.format("resourcePack.incompatible.confirm.title");
					String s = I18n.format("resourcePack.incompatible.confirm." + (packFormat > 1 ? "new" : "old"));
					getMc().displayGuiScreen(new YesNoScreen((result, id) -> {
						List<ResourcePackListEntry> list2 = ResourcePackListEntry.this.resourcePacksScreen.getListContaining(ResourcePackListEntry.this);
						getMc().displayGuiScreen(ResourcePackListEntry.this.resourcePacksScreen);

						if (result) {
							list2.remove(ResourcePackListEntry.this);
							resourcePacksScreen.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
						}
					}, s1, s, 0));
				} else {
					this.resourcePacksScreen.getListContaining(this).remove(this);
					this.resourcePacksScreen.getSelectedResourcePacks().add(0, this);
				}

				return true;
			}

			if (p_148278_5_ < 16 && this.func_148308_f()) {
				this.resourcePacksScreen.getListContaining(this).remove(this);
				this.resourcePacksScreen.getAvailableResourcePacks().add(0, this);
				this.resourcePacksScreen.markChanged();
				return true;
			}

			if (p_148278_5_ > 16 && p_148278_6_ < 16 && this.func_148314_g()) {
				List<ResourcePackListEntry> list1 = this.resourcePacksScreen.getListContaining(this);
				int k = list1.indexOf(this);
				list1.remove(this);
				list1.add(k - 1, this);
				this.resourcePacksScreen.markChanged();
				return true;
			}

			if (p_148278_5_ > 16 && p_148278_6_ > 16 && this.func_148307_h()) {
				List<ResourcePackListEntry> list = this.resourcePacksScreen.getListContaining(this);
				int i = list.indexOf(this);
				list.remove(this);
				list.add(i + 1, this);
				this.resourcePacksScreen.markChanged();
				return true;
			}
		}

		return false;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

	}

	@Override
	public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

	}
}
