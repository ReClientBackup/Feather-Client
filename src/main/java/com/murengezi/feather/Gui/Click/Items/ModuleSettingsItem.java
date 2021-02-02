package com.murengezi.feather.Gui.Click.Items;

import com.murengezi.feather.Gui.Click.Item;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.Setting.Setting;
import com.murengezi.feather.Module.Setting.Settings.BooleanSetting;
import com.murengezi.feather.Module.Setting.Settings.ModeSetting;
import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-27 at 13:51
 */
public class ModuleSettingsItem extends Item {

	private final Module module;
	private final Setting setting;

	public ModuleSettingsItem(Module module, Setting setting) {
		this.module = module;
		this.setting = setting;
	}

	@Override
	public void render(int mouseX, int mouseY) {
		GUI.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MIN_VALUE);
		switch (setting.getType()) {
			case BOOLEAN:
				BooleanSetting booleanSetting = (BooleanSetting) setting;
				GUI.drawRect(getX() + 4, getY() + 2, getX() + 14, getY() + 12, Integer.MIN_VALUE);
				GlStateManager.pushMatrix();
				float scale = 0.5f;
				GlStateManager.scale(scale, scale, scale);
				getFr().drawStringWithShadow(booleanSetting.getName(), (getX() + 18) * (1 / scale), (getY() + 5) * (1/scale), 0xffffff);
				GlStateManager.popMatrix();
				if (booleanSetting.getValue()) {
					getFr().drawStringWithShadow("✔", getX() + 5.5f, getY() + 2, 0xffffff);
				}
				break;
			case NUMBER:
				break;
			case MODE:
				getFr().drawStringWithShadow("§7" + setting.getName() + ": " + ((ModeSetting)setting).getValue(), getX() + 4, getY() + 3, 0xffffff);
				break;
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (isMouseOn(mouseX, mouseY)) {
			if (mouseButton == 0) {
				switch (setting.getType()) {
					case BOOLEAN:
						BooleanSetting booleanSetting = (BooleanSetting) setting;
						booleanSetting.setValue(!booleanSetting.getValue());
						break;
					case NUMBER:
						break;
					case MODE:
						ModeSetting modeSetting = (ModeSetting) setting;
						if (modeSetting.getModes().indexOf(modeSetting.getValue()) + 1 == modeSetting.getModes().size()) {
							modeSetting.setValue(modeSetting.getModes().get(0));
						} else {
							modeSetting.setValue(modeSetting.getModes().get(modeSetting.getModes().indexOf(modeSetting.getValue()) + 1));
						}
						break;
				}
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

	}

	public Module getModule() {
		return module;
	}
}
