package com.murengezi.feather.Util;

import com.murengezi.minecraft.potion.Potion;
import net.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-10 at 00:06
 */
public class MinecraftUtils {

    public static Minecraft getMc() {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getPlayer() {
        return getMc().thePlayer;
    }

    public static WorldClient getWorld() {
        return getMc().theWorld;
    }

    public static FontRenderer getFr() {
        return getMc().fontRendererObj;
    }

    public static GameSettings getGs() {
        return getMc().gameSettings;
    }

    public static LanguageManager getLm() {
        return getMc().getLanguageManager();
    }

    public static boolean canPlayerSprint() {
        return !getPlayer().isSprinting()
                && !getPlayer().isSneaking()
                && !getPlayer().isCollidedHorizontally
                && !getPlayer().isBlocking()
                && !getPlayer().isDead
                && !getPlayer().isRiding()
                && !getPlayer().isUsingItem()
                && !getPlayer().isPotionActive(Potion.blindness)
                && getPlayer().getFoodStats().getFoodLevel() > 6
                && getPlayer().moveForward > 0.0f;
    }

}
