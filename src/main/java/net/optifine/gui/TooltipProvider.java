package net.optifine.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;

import java.awt.*;

public interface TooltipProvider
{
    Rectangle getTooltipBounds(Screen var1, int var2, int var3);

    String[] getTooltipLines(GuiButton var1, int var2);

    boolean isRenderBorder();
}
