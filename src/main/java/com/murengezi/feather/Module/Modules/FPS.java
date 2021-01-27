package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-26 at 13:56
 */
@ModuleInfo(name = "FPS", version = "1.0.0", description = "", enabled = true)
public class FPS extends Adjustable {

	public FPS() {
		super(10, getFr().FONT_HEIGHT);
	}

	@EventTarget
	public void onRender(RenderOverlayEvent event) {
		ScaledResolution resolution = event.getScaledResolution();
		int x = getX(resolution.getScaledWidth());
		int y = getY(resolution.getScaledHeight());
		String fps = "FPS: " + Minecraft.getDebugFPS();
		Gui.drawRect(x, y, x + getFr().getStringWidth(fps), y + getFr().FONT_HEIGHT, Integer.MIN_VALUE);
		getFr().drawStringWithShadow(fps, x, y, 0xffffff);
		setWidth(getFr().getStringWidth(fps));
	}

}
