package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.feather.Module.Setting.Settings.ModeSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-26 at 13:56
 */
@ModuleInfo(name = "FPS", version = "1.0.0", description = "", enabled = true)
public class FPS extends Adjustable {

	public FPS() {
		addSetting(new ModeSetting("Mode", this, Arrays.asList("Normal", "Fraps"), "Fraps"));
	}

	@EventTarget
	public void onRender(RenderOverlayEvent event) {
		float x = getX();
		float y = getY();

		switch (getModeSetting("Mode").getValue()) {
			case "Normal":
				String fps = "FPS: " + Minecraft.getDebugFPS();
				Gui.drawRect(x, y, x + getFr().getStringWidth(fps), y + getFr().FONT_HEIGHT, Integer.MIN_VALUE);
				getFr().drawStringWithShadow(fps, x, y, 0xffffff);
				setWidth(getFr().getStringWidth(fps));
				break;
			case "Fraps":
				getFr().drawString("" + EnumChatFormatting.BLACK + Minecraft.getDebugFPS(), x + 1, y + 2, 0xffffff);
				getFr().drawString("" + EnumChatFormatting.BLACK + Minecraft.getDebugFPS(), x + 1, y, 0xffffff);
				getFr().drawString("" + EnumChatFormatting.BLACK + Minecraft.getDebugFPS(), x + 2, y + 1, 0xffffff);
				getFr().drawString("" + EnumChatFormatting.BLACK + Minecraft.getDebugFPS(), x, y + 1, 0xffffff);
				getFr().drawString("" + EnumChatFormatting.YELLOW + Minecraft.getDebugFPS(), x + 1, y + 1, 0xffffff);

				setHeight(getFr().FONT_HEIGHT);
				setWidth(getFr().getStringWidth("" + Minecraft.getDebugFPS()) + 1);
				break;
		}

	}

}
