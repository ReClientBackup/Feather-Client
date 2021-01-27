package com.murengezi.feather.Gui.Click.Items;

import com.murengezi.feather.Feather;
import com.murengezi.feather.Gui.Click.Item;
import com.murengezi.feather.Theme.Theme;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 12:28
 */
public class ThemeItem extends Item {

    private final Theme theme;

    public ThemeItem(Theme theme) {
        this.theme = theme;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        Gui.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MIN_VALUE);
        Gui.drawRect(getX() + 4, getY() + 2, getX() + 14, getY() + 12, Integer.MIN_VALUE);
        GlStateManager.pushMatrix();
        float scale = 0.5f;
        GlStateManager.scale(scale, scale, scale);
        getFr().drawStringWithShadow(getTheme().getName(), (getX() + 18) * (1 / scale), (getY() + 5) * (1/scale), 0xffffff);
        GlStateManager.popMatrix();
        if (getTheme() == Feather.getThemeManager().getActiveTheme()) {
            getFr().drawStringWithShadow("✔", getX() + 5.5f, getY() + 2, 0xffffff);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOn(mouseX, mouseY)) {
            if (mouseButton == 0) {
                Feather.getThemeManager().setActiveTheme(getTheme());
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    public Theme getTheme() {
        return theme;
    }
}
