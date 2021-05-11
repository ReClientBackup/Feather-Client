package com.murengezi.minecraft.client.renderer;

import com.murengezi.minecraft.potion.Potion;
import com.murengezi.minecraft.potion.PotionEffect;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;

import java.util.Collection;

public abstract class InventoryEffectRenderer extends GuiContainer {

	private boolean hasActivePotionEffects;

	public InventoryEffectRenderer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	public void initGui() {
		super.initGui();
		this.updateActivePotionEffects();
	}

	protected void updateActivePotionEffects() {
		this.guiLeft = (this.width - this.xSize) / 2;
		this.hasActivePotionEffects = !getPlayer().getActivePotionEffects().isEmpty();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.hasActivePotionEffects) {
			this.drawActivePotionEffects();
		}
	}

	private void drawActivePotionEffects() {
		int x = this.guiLeft - 124;
		int y = this.guiTop;
		Collection<PotionEffect> collection = getPlayer().getActivePotionEffects();

		if (!collection.isEmpty()) {
			GlStateManager.colorAllMax();
			GlStateManager.disableLighting();
			int l = 33;

			if (collection.size() > 5) {
				l = 132 / (collection.size() - 1);
			}

			for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
				Potion potion = Potion.potionTypes[effect.getPotionID()];
				GlStateManager.colorAllMax();
				getMc().getTextureManager().bindTexture(inventoryBackground);
				this.drawTexturedModalRect(x, y, 0, 166, 140, 32);

				if (potion.hasStatusIcon()) {
					int i1 = potion.getStatusIconIndex();
					this.drawTexturedModalRect(x + 6, y + 7, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
				}

				String s1 = I18n.format(potion.getName());

				if (effect.getAmplifier() == 1) {
					s1 = s1 + " " + I18n.format("enchantment.level.2");
				} else if (effect.getAmplifier() == 2) {
					s1 = s1 + " " + I18n.format("enchantment.level.3");
				} else if (effect.getAmplifier() == 3) {
					s1 = s1 + " " + I18n.format("enchantment.level.4");
				}

				getFr().drawStringWithShadow(s1, (float) (x + 10 + 18), (float) (y + 6), 16777215);
				String s = Potion.getDurationString(effect);
				getFr().drawStringWithShadow(s, (float) (x + 10 + 18), (float) (y + 6 + 10), 8355711);
				y += l;
			}
		}
	}

}
