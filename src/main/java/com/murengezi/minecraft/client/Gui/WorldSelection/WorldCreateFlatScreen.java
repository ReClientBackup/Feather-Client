package com.murengezi.minecraft.client.Gui.WorldSelection;

import com.murengezi.minecraft.client.Gui.GuiButton;
import com.murengezi.minecraft.client.Gui.Screen;
import com.murengezi.minecraft.client.Gui.fGuiSlot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-25 at 11:58
 */
public class WorldCreateFlatScreen extends Screen {

	private final WorldCreateScreen parentScreen;
	private FlatGeneratorInfo theFlatGeneratorInfo = FlatGeneratorInfo.getDefaultFlatGenerator();
	private WorldCreateFlatScreen.Details createFlatWorldListSlotGui;

	private static final int ADDLAYER = 0;
	private static final int EDITLAYER = 1;
	private static final int REMOVELAYER = 2;
	private static final int DONE = 3;
	private static final int PRESETS = 4;
	private static final int CANCEL = 5;

	public WorldCreateFlatScreen(WorldCreateScreen parentScreen, String p_i1029_2_) {
		this.parentScreen = parentScreen;
		this.func_146383_a(p_i1029_2_);
	}

	public String func_146384_e()
	{
		return this.theFlatGeneratorInfo.toString();
	}

	public void func_146383_a(String p_146383_1_) {
		this.theFlatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(p_146383_1_);
	}

	@Override
	public void initGui() {
		this.createFlatWorldListSlotGui = new WorldCreateFlatScreen.Details();
		addButton(new GuiButton(ADDLAYER, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.addLayer") + " (NYI)"));
		addButton(new GuiButton(EDITLAYER, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.editLayer") + " (NYI)"));
		addButton(new GuiButton(REMOVELAYER, this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer")));
		addButton(new GuiButton(DONE, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
		addButton(new GuiButton(PRESETS, this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets")));
		addButton(new GuiButton(CANCEL, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
		getButton(ADDLAYER).setVisible(false);
		getButton(EDITLAYER).setVisible(false);
		this.theFlatGeneratorInfo.func_82645_d();
		this.func_146375_g();
		super.initGui();
	}

	@Override
	public void handleMouseInput() throws IOException {
		this.createFlatWorldListSlotGui.handleMouseInput();
		super.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		int i = this.theFlatGeneratorInfo.getFlatLayers().size() - this.createFlatWorldListSlotGui.field_148228_k - 1;

		switch (button.getId()) {
			case ADDLAYER:
			case EDITLAYER:
				break;
			case REMOVELAYER:
				if (this.func_146382_i()) {
					this.theFlatGeneratorInfo.getFlatLayers().remove(i);
					this.createFlatWorldListSlotGui.field_148228_k = Math.min(this.createFlatWorldListSlotGui.field_148228_k, this.theFlatGeneratorInfo.getFlatLayers().size() - 1);
				}
				break;
			case DONE:
				parentScreen.chunkProviderSettingsJson = this.func_146384_e();
				changeScreen(parentScreen);
				break;
			case PRESETS:
				changeScreen(new WorldFlatPresets(this));
				break;
			case CANCEL:
				changeScreen(parentScreen);
				break;
		}

		this.theFlatGeneratorInfo.func_82645_d();
		this.func_146375_g();
		super.actionPerformed(button);
	}

	public void func_146375_g() {
		boolean flag = this.func_146382_i();
		getButton(REMOVELAYER).setEnabled(flag);
		getButton(EDITLAYER).setEnabled(flag);
		getButton(EDITLAYER).setEnabled(false);
		getButton(ADDLAYER).setEnabled(false);
	}

	private boolean func_146382_i() {
		return this.createFlatWorldListSlotGui.field_148228_k > -1 && this.createFlatWorldListSlotGui.field_148228_k < this.theFlatGeneratorInfo.getFlatLayers().size();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground(mouseX, mouseY, 60);
		this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRendererObj, I18n.format("createWorld.customize.flat.title"), this.width / 2, 8, 16777215);
		int i = this.width / 2 - 92 - 16;

		this.drawString(this.fontRendererObj, I18n.format("createWorld.customize.flat.tile"), i, 32, 16777215);
		String s = I18n.format("createWorld.customize.flat.height");
		this.drawString(this.fontRendererObj, s, i + 2 + 213 - this.fontRendererObj.getStringWidth(s), 32, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	class Details extends fGuiSlot {
		public int field_148228_k = -1;

		public Details() {
			super(WorldCreateFlatScreen.this.width, WorldCreateFlatScreen.this.height, 43, WorldCreateFlatScreen.this.height - 60, 24);
		}

		private void func_148225_a(int p_148225_1_, int p_148225_2_, ItemStack p_148225_3_) {
			this.func_148226_e(p_148225_1_ + 1, p_148225_2_ + 1);
			GlStateManager.enableRescaleNormal();

			if (p_148225_3_ != null && p_148225_3_.getItem() != null) {
				RenderHelper.enableGUIStandardItemLighting();
				WorldCreateFlatScreen.this.itemRender.renderItemIntoGUI(p_148225_3_, p_148225_1_ + 2, p_148225_2_ + 2);
				RenderHelper.disableStandardItemLighting();
			}

			GlStateManager.disableRescaleNormal();
		}

		private void func_148226_e(int p_148226_1_, int p_148226_2_) {
			this.func_148224_c(p_148226_1_, p_148226_2_, 0, 0);
		}

		private void func_148224_c(int p_148224_1_, int p_148224_2_, int p_148224_3_, int p_148224_4_) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			getMc().getTextureManager().bindTexture(Gui.statIcons);
			float f = 0.0078125F;
			float f1 = 0.0078125F;
			int i = 18;
			int j = 18;
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldrenderer.pos(p_148224_1_, p_148224_2_ + 18, WorldCreateFlatScreen.this.zLevel).tex((float)(p_148224_3_) * 0.0078125F, (float)(p_148224_4_ + 18) * 0.0078125F).func_181675_d();
			worldrenderer.pos(p_148224_1_ + 18, p_148224_2_ + 18, WorldCreateFlatScreen.this.zLevel).tex((float)(p_148224_3_ + 18) * 0.0078125F, (float)(p_148224_4_ + 18) * 0.0078125F).func_181675_d();
			worldrenderer.pos(p_148224_1_ + 18, p_148224_2_, WorldCreateFlatScreen.this.zLevel).tex((float)(p_148224_3_ + 18) * 0.0078125F, (float)(p_148224_4_) * 0.0078125F).func_181675_d();
			worldrenderer.pos(p_148224_1_, p_148224_2_, WorldCreateFlatScreen.this.zLevel).tex((float)(p_148224_3_) * 0.0078125F, (float)(p_148224_4_) * 0.0078125F).func_181675_d();
			tessellator.draw();
		}

		@Override
		protected int getSize() {
			return WorldCreateFlatScreen.this.theFlatGeneratorInfo.getFlatLayers().size();
		}

		@Override
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			this.field_148228_k = slotIndex;
			WorldCreateFlatScreen.this.func_146375_g();
		}

		@Override
		protected boolean isSelected(int slotIndex) {
			return slotIndex == this.field_148228_k;
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected void drawSlot(int entryID, int x, int y, int p_180791_4_, int mouseXIn, int mouseYIn) {
			FlatLayerInfo flatlayerinfo = WorldCreateFlatScreen.this.theFlatGeneratorInfo.getFlatLayers().get(WorldCreateFlatScreen.this.theFlatGeneratorInfo.getFlatLayers().size() - entryID - 1);
			IBlockState iblockstate = flatlayerinfo.func_175900_c();
			Block block = iblockstate.getBlock();
			Item item = Item.getItemFromBlock(block);
			ItemStack itemstack = block != Blocks.air && item != null ? new ItemStack(item, 1, block.getMetaFromState(iblockstate)) : null;
			String s = itemstack == null ? "Air" : item.getItemStackDisplayName(itemstack);

			if (item == null) {
				if (block != Blocks.water && block != Blocks.flowing_water) {
					if (block == Blocks.lava || block == Blocks.flowing_lava) {
						item = Items.lava_bucket;
					}
				} else {
					item = Items.water_bucket;
				}

				if (item != null) {
					itemstack = new ItemStack(item, 1, block.getMetaFromState(iblockstate));
					s = block.getLocalizedName();
				}
			}

			this.func_148225_a(x, y, itemstack);
			WorldCreateFlatScreen.this.fontRendererObj.drawString(s, x + 18 + 5, y + 3, 16777215);
			String s1;

			if (entryID == 0) {
				s1 = I18n.format("createWorld.customize.flat.layer.top", flatlayerinfo.getLayerCount());
			} else if (entryID == WorldCreateFlatScreen.this.theFlatGeneratorInfo.getFlatLayers().size() - 1) {
				s1 = I18n.format("createWorld.customize.flat.layer.bottom", flatlayerinfo.getLayerCount());
			} else {
				s1 = I18n.format("createWorld.customize.flat.layer", flatlayerinfo.getLayerCount());
			}

			WorldCreateFlatScreen.this.fontRendererObj.drawString(s1, x + 2 + 213 - WorldCreateFlatScreen.this.fontRendererObj.getStringWidth(s1), y + 3, 16777215);
		}

		@Override
		protected int getScrollBarX()
		{
			return this.width - 70;
		}
	}
	
}
