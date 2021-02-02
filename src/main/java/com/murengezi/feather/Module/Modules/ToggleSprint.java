package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.EntityHitEvent;
import com.murengezi.feather.Event.PlayerUpdateEvent;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.minecraft.client.gui.GUI;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 01:17
 */
@ModuleInfo(name = "ToggleSprint", description = "A mod to always have sprint on.", version = "1.0.0", enabled = true)
public class ToggleSprint extends Adjustable {

    private static final String text = "Sprint (Toggled)";

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (canPlayerSprint()) {
            getPlayer().setSprinting(true);
        }
    }

    @EventTarget
    public void onEntityHit(EntityHitEvent event) {
        if (getPlayer().isSprinting()) {
            getPlayer().setSprinting(false);
        }
    }

    @EventTarget
    public void onRender(RenderOverlayEvent event) {
        GUI.drawRect(getX(), getY(), getX() + getFr().getStringWidth(text) + 3, getY() + getFr().FONT_HEIGHT + 3, Integer.MIN_VALUE);
        getFr().drawStringWithShadow(text, getX() + 2, getY() + 2, 0xffffffff);
        setWidth(getFr().getStringWidth(text) + 3);
        setHeight(getFr().FONT_HEIGHT + 3);
    }

}
