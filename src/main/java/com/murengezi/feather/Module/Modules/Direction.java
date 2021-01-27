package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Feather;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-11 at 21:03
 */

@ModuleInfo(name = "Direction", description = "Shows the direction you are facing.", version = "1.0.0", enabled = true)
public class Direction extends Adjustable {


    public Direction() {
        super(40, 40);
    }

    @EventTarget
    public void onRender(RenderOverlayEvent event) {
        ScaledResolution resolution = event.getScaledResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        Gui.drawRect(getX(width), getY(height), getX(width) + getWidth(), getY(height) + getHeight(), Integer.MIN_VALUE);

        /*
         * FONT HEIGHT 9.0f
         */
        int color = Feather.getThemeManager().getActiveTheme().getColor().getRGB();
        getFr().drawCenteredString((getPlayer().getHorizontalFacing() == EnumFacing.NORTH ? "" : "§7") + "N§r", getX(width) + ((float)getWidth() / 2) - 0.5f, getY(height) + 2, color);
        getFr().drawCenteredString((getPlayer().getHorizontalFacing() == EnumFacing.SOUTH ? "" : "§7") + "S§r", getX(width) + ((float)getWidth() / 2) - 0.5f, getY(height) + getHeight() - 10, color);
        getFr().drawCenteredString((getPlayer().getHorizontalFacing() == EnumFacing.WEST ? "" : "§7") + "W§r", getX(width) + 4, getY(height) + ((float)getHeight() / 2) - 4.0f, color);
        getFr().drawCenteredString((getPlayer().getHorizontalFacing() == EnumFacing.EAST ? "" : "§7") + "E§r", getX(width) + getWidth() - 6, getY(height) + ((float)getHeight() / 2) - 4.0f, color);

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.translate(getX(width) + (float)getWidth() / 2f, getY(height) + getHeight() / 2f, 0);
        GlStateManager.rotate(getPlayer().rotationYaw - 180, 0, 0, 1);
        ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
        getMc().getTextureManager().bindTexture(mapIcons);
        GlStateManager.translate(-3.5f, -3.5f, 0);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 1, 0, 7, 7, 32, 32);
        GlStateManager.popMatrix();
    }

}
