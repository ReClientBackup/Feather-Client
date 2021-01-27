package net.optifine.gui;

import java.awt.Rectangle;
import com.murengezi.minecraft.client.Gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public interface TooltipProvider
{
    Rectangle getTooltipBounds(GuiScreen var1, int var2, int var3);

    String[] getTooltipLines(GuiButton var1, int var2);

    boolean isRenderBorder();
}
