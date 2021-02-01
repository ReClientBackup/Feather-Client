package com.murengezi.feather.Gui.Click.Frames;

import com.murengezi.feather.Gui.Click.Frame;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 12:18
 */
public class ThemesFrame extends Frame {

    public ThemesFrame(int x, int y) {
        super("Themes", x, y);
    }

    @Override
    public void render(int mouseX, int mouseY, ScaledResolution scaledResolution) {
        Gui.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MAX_VALUE);
        getFr().drawCenteredString(getTitle(), getX() + 40, getY() + 3, 0xffffff);
        super.render(mouseX, mouseY, scaledResolution);
    }
}
