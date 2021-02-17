package com.murengezi.chocolate.Gui.Click.Frames;

import com.murengezi.chocolate.Gui.Click.Frame;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.ScaledResolution;

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
        GUI.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MAX_VALUE);
        getFr().drawCenteredString(getTitle(), getX() + 40, getY() + 3, 0xffffff);
        super.render(mouseX, mouseY, scaledResolution);
    }
}
