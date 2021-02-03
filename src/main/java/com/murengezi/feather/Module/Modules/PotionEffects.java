package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Feather;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.feather.Module.Setting.Settings.BooleanSetting;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.potion.Potion;
import com.murengezi.minecraft.potion.PotionEffect;
import com.murengezi.minecraft.potion.PotionHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-03 at 09:28
 */
@ModuleInfo(name = "Potion Effects", description = "", version = "1.0.0", enabled = true)
public class PotionEffects extends Adjustable {

	int blink;

	public PotionEffects() {
		addSetting(new BooleanSetting("Background", this, false));

	}

	@EventTarget
	public void onRender(RenderOverlayEvent event) {
		Collection<PotionEffect> collection = getPlayer().getActivePotionEffects();

		if (!collection.isEmpty()) {
			GlStateManager.disableLighting();
			int offsetY = 0;

			if (collection.size() > 5) {
				offsetY = 132 / (collection.size() - 1);
			}

			for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
				Potion potion = Potion.potionTypes[effect.getPotionID()];
				getMc().getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				if (getBooleanSetting("Background").getValue()) {
					GUI.drawModalRectWithCustomSizedTexture(getX(), getY() + offsetY, 0, 166, 120, 32, 256, 256);
				}

				if (potion.hasStatusIcon()) {
					int iconIndex = potion.getStatusIconIndex();
					GUI.drawModalRectWithCustomSizedTexture(getX() + 6, getY() + 7 + offsetY, iconIndex % 8 * 18, (float)(198 + iconIndex / 8 * 18), 18, 18, 256, 256);
				}

				String s1 = I18n.format(potion.getName());

				switch (effect.getAmplifier()) {
					case 0:
						s1 = s1 + " " + I18n.format("enchantment.level.1");
						break;
					case 1:
						s1 = s1 + " " + I18n.format("enchantment.level.2");
						break;
					case 2:
						s1 = s1 + " " + I18n.format("enchantment.level.3");
						break;
					case 3:
						s1 = s1 + " " + I18n.format("enchantment.level.4");
						break;
				}

				getFr().drawStringWithShadow(s1, getX() + 10 + 18, getY() + 6 + offsetY, 0xffffff);
				String s = Potion.getDurationString(effect);
				getFr().drawStringWithShadow(s, getX() + 10 + 18, getY() + 6 + 10 + offsetY, Feather.getThemeManager().getActiveTheme().getColor().getRGB());
				offsetY += 33;
			}

			setHeight((float)offsetY);
			setWidth(166);

			blink++;
			if (blink > 20) {
				blink = 0;
			}
		}
	}

}
