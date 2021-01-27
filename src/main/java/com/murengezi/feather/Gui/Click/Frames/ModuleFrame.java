package com.murengezi.feather.Gui.Click.Frames;

import com.murengezi.feather.Feather;
import com.murengezi.feather.Gui.Click.Frame;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Util.TimerUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 19:30
 */
public class ModuleFrame extends Frame {

    Module module;
    TimerUtil hoverTimer;
    int hoverX, hoverY;

    public ModuleFrame(Module module, int x, int y) {
        super(module.getName(), x, y);
        this.module = module;
        hoverTimer = new TimerUtil();
    }

    @Override
    public void render(int mouseX, int mouseY, ScaledResolution scaledResolution) {
        Gui.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MAX_VALUE);
        getFr().drawCenteredString(getTitle(), getX() + 40, getY() + 3, 0xffffff);

        if (isMouseOn(mouseX, mouseY)) {
            if (hoverX == mouseX && hoverY == mouseY && !isSelected()) {
                if (hoverTimer.hasPassed(1000)) {
                    Gui.drawRect(mouseX, mouseY - getFr().FONT_HEIGHT - 4, mouseX + getFr().getStringWidth(module.getDescription()) + 4, mouseY, Integer.MIN_VALUE);
                    getFr().drawStringWithShadow(module.getDescription(), mouseX + 2, mouseY - getFr().FONT_HEIGHT - 1.5f, 0xffffffff);
                }
            } else {
                hoverX = mouseX;
                hoverY = mouseY;
                hoverTimer.reset();
            }
        }
        super.render(mouseX, mouseY, scaledResolution);
    }
}
