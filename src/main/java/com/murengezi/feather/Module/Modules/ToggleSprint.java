package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.EntityHitEvent;
import com.murengezi.feather.Event.PlayerUpdateEvent;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.feather.Module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 01:17
 */
@ModuleInfo(name = "ToggleSprint", description = "A mod to always have sprint on.", version = "1.0.0", keyBind = Keyboard.KEY_V, enabled = true)
public class ToggleSprint extends Adjustable {

    private static final String text = "Sprint (Toggled)";

    public ToggleSprint() {
        super(getFr().getStringWidth(text) + 3, getFr().FONT_HEIGHT + 3);
    }

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
        ScaledResolution resolution = event.getScaledResolution();
        int x = getX(resolution.getScaledWidth());
        int y = getY(resolution.getScaledHeight());
        Gui.drawRect(x, y, x + getFr().getStringWidth(text) + 3, y + getFr().FONT_HEIGHT + 3, Integer.MIN_VALUE);
        getFr().drawStringWithShadow(text, x + 2, y + 2, 0xffffffff);
    }

}
