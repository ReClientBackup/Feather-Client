package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderCrosshairEvent;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.ModuleInfo;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-28 at 11:11
 */
@ModuleInfo(name = "Crosshair", description = "", version = "1.0.0", enabled = true)
public class Crosshair extends Module {

	@EventTarget
	public void renderCrosshair(RenderCrosshairEvent event) {
		event.setCancelled(true);

	}

	@EventTarget
	public void renderCustomCrosshair(RenderOverlayEvent event) {

	}

}
